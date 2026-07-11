package com.example.Proyecto.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpSession;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.ComprobanteEntity;
import com.example.Proyecto.Entity.DetallePedidoEntity;
import com.example.Proyecto.Entity.DireccionEntity;
import com.example.Proyecto.Entity.PagoEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.repository.ComprobanteRepository;
import com.example.Proyecto.repository.DetallePedidoRepository;
import com.example.Proyecto.repository.DireccionRepository;
import com.example.Proyecto.repository.PagoRepository;
import com.example.Proyecto.repository.PedidoRepository;
import com.example.Proyecto.repository.ProductoRepository;

class PedidoControllerTest {

    @Test
    void checkoutGuardaPedidoDireccionComprobanteDetalleYPago() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        DetallePedidoRepository detallePedidoRepository = mock(DetallePedidoRepository.class);
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        DireccionRepository direccionRepository = mock(DireccionRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        ComprobanteRepository comprobanteRepository = mock(ComprobanteRepository.class);
        PagoRepository pagoRepository = mock(PagoRepository.class);

        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(7L);
        cliente.setNombre("Andres");
        cliente.setCorreo("andres@test.com");

        ProductosEntity producto = new ProductosEntity();
        producto.setId(1L);
        producto.setNombre("1/4 Pollo a la Brasa");
        producto.setPrecio(18.0);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("usuarioLogueado", cliente);
        session.setAttribute("cart", List.of(new CartItem("1", "1/4 Pollo a la Brasa", 18.0, 2)));

        when(clienteRepository.save(any(ClienteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(direccionRepository.findFirstByCliente_IdOrderByIdAsc(7L)).thenReturn(Optional.empty());
        when(direccionRepository.save(any(DireccionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(comprobanteRepository.save(any(ComprobanteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(PedidoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(detallePedidoRepository.save(any(DetallePedidoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pagoRepository.save(any(PagoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoController controller = new PedidoController(
                pedidoRepository,
                detallePedidoRepository,
                productoRepository,
                direccionRepository,
                clienteRepository,
                comprobanteRepository,
                pagoRepository);

        String respuesta = controller.procesarPedido(
                "Andres",
                "Quispe",
                "12345678",
                "987654321",
                "DELIVERY",
                "Av. Peru 123",
                "Los Olivos",
                "Lima",
                "Puerta roja",
                "Llamar al llegar",
                "BOLETA",
                "",
                "",
                "",
                "YAPE",
                session);

        ArgumentCaptor<DireccionEntity> direccionCaptor = ArgumentCaptor.forClass(DireccionEntity.class);
        ArgumentCaptor<ComprobanteEntity> comprobanteCaptor = ArgumentCaptor.forClass(ComprobanteEntity.class);
        ArgumentCaptor<PedidoEntity> pedidoCaptor = ArgumentCaptor.forClass(PedidoEntity.class);
        ArgumentCaptor<DetallePedidoEntity> detalleCaptor = ArgumentCaptor.forClass(DetallePedidoEntity.class);
        ArgumentCaptor<PagoEntity> pagoCaptor = ArgumentCaptor.forClass(PagoEntity.class);

        verify(direccionRepository).save(direccionCaptor.capture());
        verify(comprobanteRepository).save(comprobanteCaptor.capture());
        verify(pedidoRepository).save(pedidoCaptor.capture());
        verify(detallePedidoRepository).save(detalleCaptor.capture());
        verify(pagoRepository).save(pagoCaptor.capture());

        DireccionEntity direccion = direccionCaptor.getValue();
        ComprobanteEntity comprobante = comprobanteCaptor.getValue();
        PedidoEntity pedido = pedidoCaptor.getValue();
        DetallePedidoEntity detalle = detalleCaptor.getValue();
        PagoEntity pago = pagoCaptor.getValue();

        assertEquals("redirect:/?pedido=exitoso", respuesta);
        assertEquals("Delivery", direccion.getAlias());
        assertEquals("Av. Peru 123", direccion.getDireccion());
        assertEquals("Puerta roja - Llamar al llegar", direccion.getReferencia());
        assertEquals("Los Olivos", direccion.getDistrito());
        assertEquals("BOLETA", comprobante.getTipo());
        assertEquals("12345678", comprobante.getNumeroDocumento());
        assertEquals("Consumidor final", comprobante.getRazonSocial());
        assertEquals(cliente, pedido.getCliente());
        assertEquals(direccion, pedido.getDireccion());
        assertEquals(comprobante, pedido.getComprobante());
        assertEquals(36.0, pedido.getTotal());
        assertNotNull(pedido.getFechaPedido());
        assertEquals(producto, detalle.getProducto());
        assertEquals(2, detalle.getCantidad());
        assertEquals("YAPE", pago.getMetodo());
        assertEquals(36.0, pago.getMonto());
        assertNotNull(pago.getFechaPago());
        assertNull(session.getAttribute("cart"));
    }
}

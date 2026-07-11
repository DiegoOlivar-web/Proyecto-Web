package com.example.Proyecto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.ComprobanteEntity;
import com.example.Proyecto.Entity.DetallePedidoEntity;
import com.example.Proyecto.Entity.DireccionEntity;
import com.example.Proyecto.Entity.PagoEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.model.CheckoutRequest;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.repository.ComprobanteRepository;
import com.example.Proyecto.repository.DetallePedidoRepository;
import com.example.Proyecto.repository.DireccionRepository;
import com.example.Proyecto.repository.PagoRepository;
import com.example.Proyecto.repository.PedidoRepository;
import com.example.Proyecto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

    public static final String ENTREGA_DELIVERY = "DELIVERY";
    public static final String ENTREGA_RECOJO = "RECOJO";
    public static final String COMPROBANTE_BOLETA = "BOLETA";
    public static final String TIENDA_DIRECCION = "Ca. Jose Galvez 390, Lima 15074";

    private static final String CIUDAD_DEFAULT = "Lima";

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final DireccionRepository direccionRepository;
    private final ClienteRepository clienteRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final PagoRepository pagoRepository;

    @Transactional
    public ClienteEntity procesarPedido(ClienteEntity usuarioLogueado, List<CartItem> cart, CheckoutRequest request) {
        ClienteEntity cliente = actualizarCliente(usuarioLogueado, request);
        DireccionEntity direccionPedido = guardarDireccion(cliente, request);
        ComprobanteEntity comprobante = guardarComprobante(cliente, direccionPedido, request);
        PedidoEntity pedidoGuardado = guardarPedido(cliente, direccionPedido, comprobante, cart);

        guardarDetallesPedido(pedidoGuardado, cart);
        guardarPago(pedidoGuardado, request.getMetodoPago());

        return cliente;
    }

    private ClienteEntity actualizarCliente(ClienteEntity cliente, CheckoutRequest request) {
        cliente.setNombre(valor(request.getNombre(), cliente.getNombre(), "Cliente"));
        cliente.setApellido(valor(request.getApellido(), cliente.getApellido(), "Sin apellido"));
        cliente.setDni(valor(limpiarDni(request.getDni()), cliente.getDni(), "00000000"));
        cliente.setTelefono(valor(request.getTelefono(), cliente.getTelefono(), "Sin telefono"));
        cliente.setDireccion(direccionCliente(request.getTipoEntrega(), request.getDireccion(), cliente.getDireccion()));
        cliente.setDistrito(valor(request.getDistrito(), cliente.getDistrito(), CIUDAD_DEFAULT));
        cliente.setReferencia(valor(referenciaCompleta(request.getReferencia(), request.getIndicaciones()), cliente.getReferencia(), "Sin referencia"));

        return clienteRepository.save(cliente);
    }

    private DireccionEntity guardarDireccion(ClienteEntity cliente, CheckoutRequest request) {
        DireccionEntity direccionEntity = direccionRepository.findFirstByCliente_IdOrderByIdAsc(cliente.getId())
                .orElseGet(DireccionEntity::new);

        boolean esRecojo = ENTREGA_RECOJO.equals(normalizarOpcion(request.getTipoEntrega()));
        direccionEntity.setCliente(cliente);
        direccionEntity.setAlias(esRecojo ? "Recojo en tienda" : "Delivery");
        direccionEntity.setDireccion(esRecojo ? TIENDA_DIRECCION : valor(request.getDireccion(), cliente.getDireccion(), "Direccion pendiente"));
        direccionEntity.setReferencia(esRecojo ? "Recojo en tienda" : valor(referenciaCompleta(request.getReferencia(), request.getIndicaciones()), null, "Sin referencia"));
        direccionEntity.setCiudad(valor(request.getCiudad(), null, CIUDAD_DEFAULT));
        direccionEntity.setDistrito(esRecojo ? CIUDAD_DEFAULT : valor(request.getDistrito(), cliente.getDistrito(), CIUDAD_DEFAULT));

        return direccionRepository.save(direccionEntity);
    }

    private ComprobanteEntity guardarComprobante(
            ClienteEntity cliente,
            DireccionEntity direccionPedido,
            CheckoutRequest request) {

        String tipo = normalizarOpcion(request.getTipoComprobante());
        if (tipo.isBlank()) {
            tipo = COMPROBANTE_BOLETA;
        }

        boolean esBoleta = COMPROBANTE_BOLETA.equals(tipo);
        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setCliente(cliente);
        comprobante.setTipo(tipo);
        comprobante.setNumeroDocumento(esBoleta
                ? valor(cliente.getDni(), null, "00000000")
                : valor(request.getNumeroDocumento(), cliente.getDni(), "00000000"));
        comprobante.setRazonSocial(esBoleta ? "Consumidor final" : valor(request.getRazonSocial(), null, "Sin razon social"));
        comprobante.setDireccionFiscal(esBoleta
                ? valor(direccionPedido.getDireccion(), cliente.getDireccion(), TIENDA_DIRECCION)
                : valor(request.getDireccionFiscal(), direccionPedido.getDireccion(), TIENDA_DIRECCION));

        return comprobanteRepository.save(comprobante);
    }

    private PedidoEntity guardarPedido(
            ClienteEntity cliente,
            DireccionEntity direccionPedido,
            ComprobanteEntity comprobante,
            List<CartItem> cart) {

        LocalDateTime ahora = LocalDateTime.now();
        PedidoEntity pedido = new PedidoEntity();
        pedido.setCliente(cliente);
        pedido.setDireccion(direccionPedido);
        pedido.setComprobante(comprobante);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(calcularTotal(cart));
        pedido.setFecha(ahora);
        pedido.setFechaPedido(ahora);
        pedido.setUsuario(nombreCompleto(cliente));

        return pedidoRepository.save(pedido);
    }

    private void guardarDetallesPedido(PedidoEntity pedido, List<CartItem> cart) {
        for (CartItem item : cart) {
            Optional<ProductosEntity> producto = buscarProducto(item);
            if (producto.isEmpty()) {
                continue;
            }

            DetallePedidoEntity detalle = new DetallePedidoEntity();
            detalle.setPedido(pedido);
            detalle.setProducto(producto.get());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());

            detallePedidoRepository.save(detalle);
        }
    }

    private void guardarPago(PedidoEntity pedido, String metodoPago) {
        PagoEntity pago = new PagoEntity();
        pago.setPedido(pedido);
        pago.setMetodo(valor(normalizarOpcion(metodoPago), null, "PENDIENTE"));
        pago.setMonto(pedido.getTotal());
        pago.setFechaPago(LocalDateTime.now());

        pagoRepository.save(pago);
    }

    private Optional<ProductosEntity> buscarProducto(CartItem item) {
        try {
            return productoRepository.findById(Long.parseLong(item.getId()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private double calcularTotal(List<CartItem> cart) {
        return cart.stream().mapToDouble(CartItem::getTotal).sum();
    }

    private String direccionCliente(String tipoEntrega, String direccion, String direccionActual) {
        if (ENTREGA_RECOJO.equals(normalizarOpcion(tipoEntrega))) {
            return TIENDA_DIRECCION;
        }

        return valor(direccion, direccionActual, "Direccion pendiente");
    }

    private String referenciaCompleta(String referencia, String indicaciones) {
        String referenciaLimpia = limpiar(referencia);
        String indicacionesLimpias = limpiar(indicaciones);

        if (referenciaLimpia.isBlank()) {
            return indicacionesLimpias;
        }
        if (indicacionesLimpias.isBlank()) {
            return referenciaLimpia;
        }

        return referenciaLimpia + " - " + indicacionesLimpias;
    }

    private String nombreCompleto(ClienteEntity cliente) {
        return (valor(cliente.getNombre(), null, "Cliente") + " " + valor(cliente.getApellido(), null, "")).trim();
    }

    private String valor(String valor, String respaldo, String defecto) {
        String limpio = limpiar(valor);
        if (!limpio.isBlank()) {
            return limpio;
        }

        String respaldoLimpio = limpiar(respaldo);
        return respaldoLimpio.isBlank() ? defecto : respaldoLimpio;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarDni(String dni) {
        return limpiar(dni).replaceAll("\\D", "");
    }

    private String normalizarOpcion(String valor) {
        return limpiar(valor).toUpperCase(Locale.ROOT);
    }
}

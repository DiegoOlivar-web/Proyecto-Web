package com.example.Proyecto.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.DireccionEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.Entity.DetallePedidoEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.repository.DireccionRepository;
import com.example.Proyecto.repository.ProductoRepository;
import com.example.Proyecto.repository.PedidoRepository;
import com.example.Proyecto.repository.DetallePedidoRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    private final DetallePedidoRepository detallePedidoRepository;

    private final ProductoRepository productoRepository;

    private final DireccionRepository direccionRepository;

    @PostMapping("/cart/confirmar")
    public String procesarPedido(HttpSession session) {
        // Recuperamos el usuario autenticado desde la sesión
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            // Si el clienteno se ha autenticado, lo redirigimos al login con un parámetro de aviso
            return "redirect:/?login=requerido";
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart != null && !cart.isEmpty()) {
            // 1. Calculamos el monto total globalizado
            double totalCarrito = cart.stream().mapToDouble(CartItem::getTotal).sum();

            // 2. Generamos y guardamos la cabecera del Pedido
            PedidoEntity nuevoPedido = new PedidoEntity();
            nuevoPedido.setCliente(usuarioLogueado);
            nuevoPedido.setDireccion(obtenerDireccionPedido(usuarioLogueado));
            nuevoPedido.setEstado("PENDIENTE");
            nuevoPedido.setTotal(totalCarrito);
            nuevoPedido.setFecha(LocalDateTime.now());
            nuevoPedido.setUsuario(usuarioLogueado.getNombre());

            // Si manejas direcciones o comprobantes por defecto, puedes setear sus IDs correspondientes aquí
            
            PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);

            // 3. Registramos los detalles de cada producto en la orden comercial
            for (CartItem item : cart) {
                DetallePedidoEntity detalle = new DetallePedidoEntity();
                detalle.setPedido(pedidoGuardado);
                productoRepository.findById(Long.parseLong(item.getId()))
                        .ifPresent(detalle::setProducto);
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecio());
                
                detallePedidoRepository.save(detalle);
            }

            // 4. Limpiamos por completo el carrito de compras dentro de la sesión web del cliente
            session.removeAttribute("cart");
            return "redirect:/?pedido=exitoso";
        }

        return "redirect:/menu";
    }

    private DireccionEntity obtenerDireccionPedido(ClienteEntity cliente) {
        if (cliente.getId() != null) {
            return direccionRepository.findFirstByCliente_IdOrderByIdAsc(cliente.getId())
                    .orElseGet(() -> crearDireccionDesdeCliente(cliente));
        }

        return crearDireccionDesdeCliente(cliente);
    }

    private DireccionEntity crearDireccionDesdeCliente(ClienteEntity cliente) {
        if (cliente.getDireccion() == null || cliente.getDireccion().isBlank()) {
            return null;
        }

        DireccionEntity direccion = new DireccionEntity();
        direccion.setCliente(cliente);
        direccion.setAlias("Principal");
        direccion.setDireccion(cliente.getDireccion());
        return direccionRepository.save(direccion);
    }
}

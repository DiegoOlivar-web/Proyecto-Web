package com.example.Proyecto.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.Entity.DetallePedidoEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.repository.PedidoRepository;
import com.example.Proyecto.repository.DetallePedidoRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoRepository pedidoRepository;

    private final DetallePedidoRepository detallePedidoRepository;

    @PostMapping("/cart/confirmar")
    public String procesarPedido(HttpSession session) {
        // Recuperamos el usuario autenticado desde la sesión
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            // Si el cliente no se ha autenticado, lo redirigimos al login con un parámetro de aviso
            return "redirect:/?login=requerido";
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart != null && !cart.isEmpty()) {
            // 1. Calculamos el monto total globalizado
            double totalCarrito = cart.stream().mapToDouble(CartItem::getTotal).sum();

            // 2. Generamos y guardamos la cabecera del Pedido
            PedidoEntity nuevoPedido = new PedidoEntity();
            nuevoPedido.setClienteId(usuarioLogueado.getId());
            nuevoPedido.setEstado("PENDIENTE");
            nuevoPedido.setTotal(totalCarrito);

            // Si manejas direcciones o comprobantes por defecto, puedes setear sus IDs correspondientes aquí
            
            PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);

            // 3. Registramos los detalles de cada producto en la orden comercial
            for (CartItem item : cart) {
                DetallePedidoEntity detalle = new DetallePedidoEntity();
                detalle.setPedidoId(pedidoGuardado.getId());
                detalle.setProductoId(Long.parseLong(item.getId()));
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
}
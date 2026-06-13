package com.example.Proyecto.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.Entity.PedidoItemEntity;
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

    // 1. Redirigir a la interfaz de pago
    @PostMapping("/cart/confirmar")
    public String mostrarInterfazPago(HttpSession session, Model model) {
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        
        // Validar si el usuario ha iniciado sesión antes de pagar
        if (usuarioLogueado == null) {
            return "redirect:/?login=requerido";
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/menu";
        }

        model.addAttribute("usuarioLogueado", usuarioLogueado);
        return "pago"; // Nombre del archivo HTML (pago.html)
    }

    // 2. Procesar la acción de "Pagar pedido"
    @PostMapping("/pedido/pagar")
    public String procesarPago(
            @RequestParam("tipoDireccion") String tipoDireccion,
            @RequestParam(value = "direccionAlternativa", required = false) String direccionAlternativa,
            @RequestParam("metodoPago") String metodoPago,
            HttpSession session) {

        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (usuarioLogueado == null || cart == null || cart.isEmpty()) {
            return "redirect:/";
        }

        // Determinar la dirección de entrega definitiva
        String direccionFinal = tipoDireccion.equals("defecto") ? usuarioLogueado.getDireccion() : direccionAlternativa;

        // Calcular el total
        double total = cart.stream().mapToDouble(CartItem::getTotal).sum();

        // Guardar la cabecera del Pedido (PedidoEntity)
        PedidoEntity nuevoPedido = new PedidoEntity();
        nuevoPedido.setClienteId(usuarioLogueado.getId());
        nuevoPedido.setEstado("Pendiente");
        nuevoPedido.setTotal(total);
        // Nota: direccion_id y comprobante_id se pueden guardar como IDs o strings según escales tu app
        nuevoPedido = pedidoRepository.save(nuevoPedido); 

        // Guardar cada ítem usando tu PedidoItemEntity o DetallePedidoEntity
        for (CartItem item : cart) {
            PedidoItemEntity itemEntity = new PedidoItemEntity();
            itemEntity.setPedido(nuevoPedido);
            itemEntity.setProductId(Long.parseLong(item.getId().replaceAll("[^0-9]", ""))); // Extrae el ID numérico
            itemEntity.setNombre(item.getNombre());
            itemEntity.setPrecio(item.getPrecio());
            itemEntity.setCantidad(item.getCantidad());
            itemEntity.setSubtotal(item.getTotal());
            // Si usas el DetallePedidoRepository clásico lo guardas aquí mapeando sus campos correspondientes
        }

        // Limpiar el carrito de la sesión tras la compra exitosa
        session.removeAttribute("cart");

        return "redirect:/?pedido=exitoso";
    }
}
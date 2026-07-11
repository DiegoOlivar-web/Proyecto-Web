package com.example.Proyecto.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.model.CheckoutRequest;
import com.example.Proyecto.service.CartService;
import com.example.Proyecto.service.PedidoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;
    private final CartService cartService;

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        ClienteEntity usuarioLogueado = obtenerUsuarioLogueado(session);

        if (usuarioLogueado == null) {
            return "redirect:/?login=requerido";
        }

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/menu";
        }

        model.addAttribute("cliente", usuarioLogueado);
        model.addAttribute("checkoutSubtotal", cartService.calcularTotal(cart));
        model.addAttribute("tiendaDireccion", PedidoService.TIENDA_DIRECCION);

        return "checkout";
    }

    @PostMapping("/cart/confirmar")
    public String redirigirCheckout(HttpSession session) {
        if (obtenerUsuarioLogueado(session) == null) {
            return "redirect:/?login=requerido";
        }

        if (cartService.getCart(session).isEmpty()) {
            return "redirect:/menu";
        }

        return "redirect:/checkout";
    }

    @PostMapping("/checkout/confirmar")
    public String procesarPedido(
            @ModelAttribute CheckoutRequest request,
            HttpSession session) {

        ClienteEntity usuarioLogueado = obtenerUsuarioLogueado(session);
        if (usuarioLogueado == null) {
            return "redirect:/?login=requerido";
        }

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/menu";
        }

        ClienteEntity cliente = pedidoService.procesarPedido(usuarioLogueado, cart, request);

        session.setAttribute("usuarioLogueado", cliente);
        session.removeAttribute("cart");

        return "redirect:/?pedido=exitoso";
    }

    private ClienteEntity obtenerUsuarioLogueado(HttpSession session) {
        return (ClienteEntity) session.getAttribute("usuarioLogueado");
    }
}

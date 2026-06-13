package com.example.Proyecto.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.model.CartItem;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CartAdvice {

    @ModelAttribute
    public void addCartAttributes(Model model, HttpSession session) {
        // 1. Inyección de atributos del Carrito de compras
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        double total = cart.stream().mapToDouble(CartItem::getTotal).sum();
        model.addAttribute("cart", cart);
        model.addAttribute("cartCount", cart.size());
        model.addAttribute("cartTotal", total);

        // 2. SOLUCIÓN COMPLETA: Atributos globales de sesión para el Navbar
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("sesionActiva", usuarioLogueado != null);
    }
}
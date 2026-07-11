package com.example.Proyecto.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.Proyecto.model.CartItem;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CartAdvice {

    @ModelAttribute
    public void addCartAttributes(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        double total = cart.stream().mapToDouble(CartItem::getTotal).sum();
        int cantidadTotal = cart.stream().mapToInt(CartItem::getCantidad).sum();
        model.addAttribute("cart", cart);
        model.addAttribute("cartCount", cantidadTotal);
        model.addAttribute("cartTotal", total);
    }
}

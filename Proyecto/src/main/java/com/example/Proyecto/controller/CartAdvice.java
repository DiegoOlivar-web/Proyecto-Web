package com.example.Proyecto.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.service.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class CartAdvice {

    private final CartService cartService;

    @ModelAttribute
    public void addCartAttributes(Model model, HttpSession session) {
        List<CartItem> cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("cartCount", cartService.calcularCantidadTotal(cart));
        model.addAttribute("cartTotal", cartService.calcularTotal(cart));
    }
}

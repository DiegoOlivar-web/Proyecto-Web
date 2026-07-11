package com.example.Proyecto.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart/add", headers = "!X-Requested-With")
    public String addToCart(
            @RequestParam String id,
            @RequestParam(defaultValue = "1") int cantidad,
            @RequestParam(defaultValue = "todos") String categoria,
            HttpSession session) {

        cartService.addItemToCart(id, cantidad, session);
        return "redirect:/menu?categoria=" + categoria;
    }

    @PostMapping(value = "/cart/add", headers = "X-Requested-With=XMLHttpRequest")
    @ResponseBody
    public Map<String, Object> addToCartAjax(
            @RequestParam String id,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session) {

        List<CartItem> cart = cartService.addItemToCart(id, cantidad, session);
        return cartService.cartPayload(cart);
    }

    @PostMapping(value = "/cart/eliminar", headers = "!X-Requested-With")
    public String eliminarItem(
            @RequestParam("idProducto") String idProducto,
            HttpSession session,
            HttpServletRequest request) {

        cartService.removeItemFromCart(idProducto, session);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping(value = "/cart/eliminar", headers = "X-Requested-With=XMLHttpRequest")
    @ResponseBody
    public Map<String, Object> eliminarItemAjax(
            @RequestParam("idProducto") String idProducto,
            HttpSession session) {

        List<CartItem> cart = cartService.removeItemFromCart(idProducto, session);
        return cartService.cartPayload(cart);
    }
}

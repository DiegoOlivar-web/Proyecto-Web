package com.example.Proyecto.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.model.CartItem;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

    private static final Map<String, CartItem> PRODUCTOS = new LinkedHashMap<>();

    static {
        PRODUCTOS.put("pollo1", new CartItem("pollo1", "1/4 Pollo a la Brasa", 18.00, 1));
        PRODUCTOS.put("pollo2", new CartItem("pollo2", "Salchipollo", 20.00, 1));
        PRODUCTOS.put("pollo3", new CartItem("pollo3", "Pocho (Pollo + Anticucho)", 20.00, 1));
        PRODUCTOS.put("pollo4", new CartItem("pollo4", "Pechuga a la Parrilla", 19.00, 1));
        PRODUCTOS.put("pollo5", new CartItem("pollo5", "1/2 Pollo a la Brasa", 32.00, 1));
        PRODUCTOS.put("pollo6", new CartItem("pollo6", "Pollo Entero a la Brasa", 58.00, 1));

        PRODUCTOS.put("parr1", new CartItem("parr1", "Chuleta de Cerdo", 18.00, 1));
        PRODUCTOS.put("parr2", new CartItem("parr2", "Churrasco", 18.00, 1));
        PRODUCTOS.put("parr3", new CartItem("parr3", "Marucha", 18.00, 1));
        PRODUCTOS.put("parr4", new CartItem("parr4", "1/2 Parrilla Especial", 46.00, 1));
        PRODUCTOS.put("parr5", new CartItem("parr5", "Parrilla TORI", 70.00, 1));
        PRODUCTOS.put("parr6", new CartItem("parr6", "Anticuchos", 15.00, 1));

        PRODUCTOS.put("combo1", new CartItem("combo1", "Combo Familiar", 72.00, 1));
        PRODUCTOS.put("combo2", new CartItem("combo2", "Combo Pareja", 52.00, 1));
        PRODUCTOS.put("combo3", new CartItem("combo3", "Combo TORI", 38.00, 1));

        PRODUCTOS.put("acom1", new CartItem("acom1", "Salchipapa", 13.00, 1));
        PRODUCTOS.put("acom2", new CartItem("acom2", "Papas Fritas", 8.00, 1));
        PRODUCTOS.put("acom3", new CartItem("acom3", "Ensalada Fresca", 9.00, 1));
        PRODUCTOS.put("acom4", new CartItem("acom4", "Chaufa de Pollo", 14.00, 1));

        PRODUCTOS.put("beb1", new CartItem("beb1", "Gaseosa (350ml)", 4.00, 1));
        PRODUCTOS.put("beb2", new CartItem("beb2", "Chicha Morada", 5.00, 1));
        PRODUCTOS.put("beb3", new CartItem("beb3", "Limonada Frozen", 7.00, 1));
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam String id,
            @RequestParam(defaultValue = "1") int cantidad,
            @RequestParam(defaultValue = "todos") String categoria,
            HttpSession session) {

        CartItem producto = PRODUCTOS.get(id);
        if (producto != null) {
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            CartItem item = null;
            for (CartItem cartItem : cart) {
                if (cartItem.getId().equals(id)) {
                    item = cartItem;
                    break;
                }
            }

            if (item != null) {
                item.setCantidad(item.getCantidad() + cantidad);
            } else {
                cart.add(new CartItem(producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad));
            }
        }

        return "redirect:/menu?categoria=" + categoria;
    }
}


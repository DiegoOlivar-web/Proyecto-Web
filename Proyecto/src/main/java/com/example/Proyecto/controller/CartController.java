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
        PRODUCTOS.put("1", new CartItem("1", "1/4 Pollo a la Brasa", 18.00, 1));
        PRODUCTOS.put("2", new CartItem("2", "Salchipollo", 20.00, 1));
        PRODUCTOS.put("3", new CartItem("3", "Pocho (Pollo + Anticucho)", 20.00, 1));
        PRODUCTOS.put("4", new CartItem("4", "Pechuga a la Parrilla", 19.00, 1));
        PRODUCTOS.put("5", new CartItem("5", "1/2 Pollo a la Brasa", 32.00, 1));
        PRODUCTOS.put("6", new CartItem("6", "Pollo Entero a la Brasa", 58.00, 1));

        PRODUCTOS.put("7", new CartItem("7", "Chuleta de Cerdo", 18.00, 1));
        PRODUCTOS.put("8", new CartItem("8", "Churrasco", 18.00, 1));
        PRODUCTOS.put("9", new CartItem("9", "Marucha", 18.00, 1));
        PRODUCTOS.put("10", new CartItem("10", "1/2 Parrilla Especial", 46.00, 1));
        PRODUCTOS.put("11", new CartItem("11", "Parrilla TORI", 70.00, 1));
        PRODUCTOS.put("12", new CartItem("12", "Anticuchos", 15.00, 1));

        PRODUCTOS.put("13", new CartItem("13", "Combo Familiar", 72.00, 1));
        PRODUCTOS.put("14", new CartItem("14", "Combo Pareja", 52.00, 1));
        PRODUCTOS.put("15", new CartItem("15", "Combo TORI", 38.00, 1));

        PRODUCTOS.put("16", new CartItem("16", "Salchipapa", 13.00, 1));
        PRODUCTOS.put("17", new CartItem("17", "Papas Fritas", 8.00, 1));
        PRODUCTOS.put("18", new CartItem("18", "Ensalada Fresca", 9.00, 1));
        PRODUCTOS.put("19", new CartItem("19", "Chaufa de Pollo", 14.00, 1));

        PRODUCTOS.put("20", new CartItem("20", "Gaseosa (350ml)", 4.00, 1));
        PRODUCTOS.put("21", new CartItem("21", "Chicha Morada", 5.00, 1));
        PRODUCTOS.put("22", new CartItem("22", "Limonada Frozen", 7.00, 1));
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

    @PostMapping("/cart/eliminar")
    public String eliminarItem(@RequestParam("idProducto") String idProducto, 
    HttpSession session, 
    jakarta.servlet.http.HttpServletRequest request) {
        // 1. Recuperamos la lista original de objetos CartItem de la sesión
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        if (cart != null) {
            // 2. Eliminamos el objeto cuyo ID coincida con el String recibido (ej. "pollo5")
            cart.removeIf(item -> item.getId().equals(idProducto));
        }
        
        // 3. Redireccionamos dinámicamente a la página desde donde el usuario clickeó (index o menú)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}


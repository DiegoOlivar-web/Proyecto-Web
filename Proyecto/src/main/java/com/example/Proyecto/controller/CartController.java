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
import jakarta.servlet.http.HttpServletRequest;


@Controller
public class CartController {

    private static final Map<String, CartItem> PRODUCTOS = new LinkedHashMap<>();

    // Inicializamos el catálogo de productos usando Strings para los IDs
    static {
        PRODUCTOS.put("1", new CartItem("1", "1/4 Pollo a la Brasa", 18.00, 1));
        PRODUCTOS.put("2", new CartItem("2", "Salchipollo", 20.00, 1));
        PRODUCTOS.put("3", new CartItem("3", "Pocho (Pollo + Anticucho)", 20.00, 1));
        PRODUCTOS.put("4", new CartItem("4", "Pechuga a la Parrilla", 19.00, 1));
        PRODUCTOS.put("5", new CartItem("5", "1/2 Pollo a la Brasa", 32.00, 1));
        PRODUCTOS.put("6", new CartItem("6", "Pollo Entero a la Brasa", 58.00, 1));
        
        // Bebidas u otros productos nuevos siguen la misma lógica
        PRODUCTOS.put("7", new CartItem("7", "Inka Cola / Coca-Cola / Sprite", 5.00, 1));
        PRODUCTOS.put("8", new CartItem("8", "Chicha Morada", 6.00, 1));
    }

    @PostMapping("/cart/agregar")
    public String agregarAlCarrito(@RequestParam("id") String id, 
                                @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
                                @RequestParam(value = "categoria", defaultValue = "todos") String categoria,
                                HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        CartItem producto = PRODUCTOS.get(id);

        if (producto != null) {
            CartItem itemEnCarrito = null;
            for (CartItem cartItem : cart) {
                // Usamos .equals() porque los IDs ahora son tipo String
                if (cartItem.getId().equals(id)) {
                    itemEnCarrito = cartItem;
                    break;
                }
            }

            if (itemEnCarrito != null) {
                itemEnCarrito.setCantidad(itemEnCarrito.getCantidad() + cantidad);
            } else {
                cart.add(new CartItem(producto.getId(), producto.getNombre(), producto.getPrecio(), cantidad));
            }
        }

        return "redirect:/menu?categoria=" + categoria;
    }

    @PostMapping("/cart/eliminar")
    public String eliminarItem(@RequestParam("idProducto") String idProducto, 
                                HttpSession session, 
                                HttpServletRequest request) {
        
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        
        if (cart != null) {
            cart.removeIf(item -> item.getId().equals(idProducto));
        }
        
        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/menu")) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }
}
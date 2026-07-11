package com.example.Proyecto.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.repository.ProductoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final ProductoRepository productoRepository;

    @PostMapping(value = "/cart/add", headers = "!X-Requested-With")
    public String addToCart(
            @RequestParam String id,
            @RequestParam(defaultValue = "1") int cantidad,
            @RequestParam(defaultValue = "todos") String categoria,
            HttpSession session) {

        addItemToCart(id, cantidad, session);
        return "redirect:/menu?categoria=" + categoria;
    }

    @PostMapping(value = "/cart/add", headers = "X-Requested-With=XMLHttpRequest")
    @ResponseBody
    public Map<String, Object> addToCartAjax(
            @RequestParam String id,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session) {

        List<CartItem> cart = addItemToCart(id, cantidad, session);
        return cartPayload(cart);
    }

    @PostMapping(value = "/cart/eliminar", headers = "!X-Requested-With")
    public String eliminarItem(
            @RequestParam("idProducto") String idProducto,
            HttpSession session,
            HttpServletRequest request) {

        removeItemFromCart(idProducto, session);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping(value = "/cart/eliminar", headers = "X-Requested-With=XMLHttpRequest")
    @ResponseBody
    public Map<String, Object> eliminarItemAjax(
            @RequestParam("idProducto") String idProducto,
            HttpSession session) {

        List<CartItem> cart = removeItemFromCart(idProducto, session);
        return cartPayload(cart);
    }

    private List<CartItem> addItemToCart(String id, int cantidad, HttpSession session) {
        List<CartItem> cart = getCart(session);
        Optional<ProductosEntity> producto = buscarProductoDisponible(id);
        if (producto.isPresent()) {
            ProductosEntity productoEntity = producto.get();
            String idProducto = productoEntity.getId().toString();
            int cantidadSegura = Math.max(cantidad, 1);
            CartItem item = null;
            for (CartItem cartItem : cart) {
                if (cartItem.getId().equals(idProducto)) {
                    item = cartItem;
                    break;
                }
            }

            if (item != null) {
                item.setCantidad(item.getCantidad() + cantidadSegura);
            } else {
                cart.add(new CartItem(
                        idProducto,
                        productoEntity.getNombre(),
                        productoEntity.getPrecio(),
                        cantidadSegura));
            }
        }
        return cart;
    }

    private List<CartItem> removeItemFromCart(String idProducto, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getId().equals(idProducto));
        return cart;
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private Map<String, Object> cartPayload(List<CartItem> cart) {
        Map<String, Object> payload = new LinkedHashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        double total = 0;
        int cantidadTotal = 0;

        for (CartItem item : cart) {
            Map<String, Object> itemPayload = new LinkedHashMap<>();
            itemPayload.put("id", item.getId());
            itemPayload.put("nombre", item.getNombre());
            itemPayload.put("cantidad", item.getCantidad());
            itemPayload.put("total", item.getTotal());
            items.add(itemPayload);
            total += item.getTotal();
            cantidadTotal += item.getCantidad();
        }

        payload.put("cartCount", cantidadTotal);
        payload.put("cartTotal", total);
        payload.put("items", items);
        return payload;
    }

    private Optional<ProductosEntity> buscarProductoDisponible(String id) {
        try {
            Long idProducto = Long.parseLong(id);
            return productoRepository.findById(idProducto)
                    .filter(producto -> !Boolean.FALSE.equals(producto.getDisponible()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}

package com.example.Proyecto.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductoRepository productoRepository;

    public List<CartItem> addItemToCart(String id, int cantidad, HttpSession session) {
        List<CartItem> cart = getCart(session);
        Optional<ProductosEntity> producto = buscarProductoDisponible(id);
        if (producto.isEmpty()) {
            return cart;
        }

        ProductosEntity productoEntity = producto.get();
        String idProducto = productoEntity.getId().toString();
        int cantidadSegura = Math.max(cantidad, 1);
        CartItem item = buscarItem(cart, idProducto);

        if (item != null) {
            item.setCantidad(item.getCantidad() + cantidadSegura);
        } else {
            cart.add(new CartItem(
                    idProducto,
                    productoEntity.getNombre(),
                    productoEntity.getPrecio(),
                    cantidadSegura));
        }

        return cart;
    }

    public List<CartItem> removeItemFromCart(String idProducto, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getId().equals(idProducto));
        return cart;
    }

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public Map<String, Object> cartPayload(List<CartItem> cart) {
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

    public double calcularTotal(List<CartItem> cart) {
        return cart.stream().mapToDouble(CartItem::getTotal).sum();
    }

    public int calcularCantidadTotal(List<CartItem> cart) {
        return cart.stream().mapToInt(CartItem::getCantidad).sum();
    }

    private CartItem buscarItem(List<CartItem> cart, String idProducto) {
        for (CartItem cartItem : cart) {
            if (cartItem.getId().equals(idProducto)) {
                return cartItem;
            }
        }
        return null;
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

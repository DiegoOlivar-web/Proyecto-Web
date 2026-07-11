package com.example.Proyecto.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.repository.ProductoRepository;
import com.example.Proyecto.service.CartService;

class CartControllerTest {

    @Test
    void agregaProductoDesdeBaseDeDatos() {
        ProductoRepository productoRepository = mock(ProductoRepository.class);
        ProductosEntity producto = new ProductosEntity();
        producto.setId(1L);
        producto.setNombre("1/4 Pollo a la Brasa");
        producto.setPrecio(18.00);
        producto.setDisponible(true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        CartService cartService = new CartService(productoRepository);
        CartController cartController = new CartController(cartService);
        MockHttpSession session = new MockHttpSession();

        Map<String, Object> payload = cartController.addToCartAjax("1", 2, session);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");

        assertEquals(2, payload.get("cartCount"));
        assertEquals(1, items.size());
        assertEquals("1", items.get(0).get("id"));
        assertEquals("1/4 Pollo a la Brasa", items.get(0).get("nombre"));
        assertEquals(2, items.get(0).get("cantidad"));
    }
}

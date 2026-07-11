package com.example.Proyecto.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductosEntity> productosFiltrados(String categoria) {
        return productoRepository.findDisponiblesConCategoria().stream()
                .filter(producto -> "todos".equalsIgnoreCase(categoria) || coincideCategoria(producto, categoria))
                .toList();
    }

    public String tituloCategoria(String categoria) {
        return switch (normalizar(categoria)) {
            case "pollo" -> "Pollos a la Brasa";
            case "parrilla" -> "Parrillas";
            case "combo" -> "Combos";
            case "acompanamiento" -> "Acompanamientos";
            case "bebida" -> "Bebidas";
            default -> "Todos los productos";
        };
    }

    private boolean coincideCategoria(ProductosEntity producto, String categoria) {
        if (producto.getCategoria() == null || producto.getCategoria().getNombre() == null) {
            return false;
        }

        String filtro = normalizar(categoria);
        String nombreCategoria = normalizar(producto.getCategoria().getNombre());

        if ("pollo".equals(filtro)) {
            return nombreCategoria.contains("pollo");
        }
        if ("parrilla".equals(filtro)) {
            return nombreCategoria.contains("parrilla");
        }
        if ("combo".equals(filtro)) {
            return nombreCategoria.contains("combo");
        }
        if ("acompanamiento".equals(filtro)) {
            return nombreCategoria.contains("acompanamiento");
        }
        if ("bebida".equals(filtro)) {
            return nombreCategoria.contains("bebida");
        }

        return nombreCategoria.contains(filtro);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}

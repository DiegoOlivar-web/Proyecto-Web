package com.example.Proyecto.controller;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class maincontroller {

    private final ProductoRepository productoRepository;

    @GetMapping("/menu")
    public String menu(@RequestParam(required = false, defaultValue = "todos") String categoria,
                       HttpSession session,
                       Model model) {
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("sesionActiva", usuarioLogueado != null);
        model.addAttribute("categoria", categoria);
        model.addAttribute("tituloCategoria", tituloCategoria(categoria));
        model.addAttribute("productosMenu", productosFiltrados(categoria));

        return "menu";
    }

    @GetMapping({"/", "/index.html", "/index"})
    public String index(@RequestParam(required = false) String login,
                        @RequestParam(required = false) String registro,
                        @RequestParam(required = false) String logout,
                        HttpSession session,
                        Model model) {
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("sesionActiva", usuarioLogueado != null);

        if (login != null) {
            model.addAttribute("loginEstado", login);
            if (usuarioLogueado != null) {
                model.addAttribute("loginNombre", usuarioLogueado.getNombre());
            }
        }
        if (registro != null) {
            model.addAttribute("registroEstado", registro);
        }
        if (logout != null) {
            model.addAttribute("logoutEstado", logout);
        }

        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros(HttpSession session, Model model) {
        ClienteEntity usuarioLogueado = (ClienteEntity) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        model.addAttribute("sesionActiva", usuarioLogueado != null);

        return "nosotros";
    }

    private List<ProductosEntity> productosFiltrados(String categoria) {
        return productoRepository.findDisponiblesConCategoria().stream()
                .filter(producto -> "todos".equalsIgnoreCase(categoria) || coincideCategoria(producto, categoria))
                .toList();
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

    private String tituloCategoria(String categoria) {
        return switch (normalizar(categoria)) {
            case "pollo" -> "Pollos a la Brasa";
            case "parrilla" -> "Parrillas";
            case "combo" -> "Combos";
            case "acompanamiento" -> "Acompañamientos";
            case "bebida" -> "Bebidas";
            default -> "Todos los productos";
        };
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

package com.example.Proyecto.controller.admin;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto.service.AdminAuthService;
import com.example.Proyecto.service.AdminProductoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/menu")
@RequiredArgsConstructor
public class AdminMenuController {
    private final AdminAuthService adminAuthService;
    private final AdminProductoService adminProductoService;

    @GetMapping
    public String verMenu(Model model, HttpSession session) {
        if (!adminAuthService.esAdmin(session)) return "redirect:/?login=requerido";

        model.addAttribute("productos", adminProductoService.listarProductos());
        model.addAttribute("categorias", adminProductoService.listarCategorias());
        return "menuAdmin";
    }

    @PostMapping
    public String guardarProducto(@RequestParam(required = false) Long id, @RequestParam String nombre,
            @RequestParam String descripcion, @RequestParam double precio, @RequestParam Long categoriaId,
            @RequestParam(defaultValue = "false") boolean disponible,
            @RequestParam(required = false) MultipartFile imagen,
            HttpSession session) throws IOException {
        if (!adminAuthService.esAdmin(session)) return "redirect:/?login=requerido";

        adminProductoService.guardar(id, nombre, descripcion, precio, categoriaId, disponible, imagen);
        return "redirect:/admin/menu";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id, HttpSession session) {
        if (!adminAuthService.esAdmin(session)) return "redirect:/?login=requerido";

        adminProductoService.eliminar(id);
        return "redirect:/admin/menu";
    }
}

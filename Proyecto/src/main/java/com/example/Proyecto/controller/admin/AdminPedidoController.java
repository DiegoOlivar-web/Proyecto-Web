package com.example.Proyecto.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.service.AdminAuthService;
import com.example.Proyecto.service.AdminPedidoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPedidoController {
    private final AdminAuthService adminAuthService;
    private final AdminPedidoService adminPedidoService;

    @GetMapping
    public String verPedidos(Model model, HttpSession session) {
        if (!adminAuthService.esAdmin(session)) return "redirect:/?login=requerido";

        var resumen = adminPedidoService.obtenerResumen();
        model.addAttribute("pedidos", resumen.pedidos());
        model.addAttribute("detallesPorPedido", resumen.detallesPorPedido());
        model.addAttribute("pendientes", resumen.pendientes());
        model.addAttribute("entregados", resumen.entregados());
        return "pedidosAdmin";
    }

    @PostMapping("/pedidos/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String estado, HttpSession session) {
        if (!adminAuthService.esAdmin(session)) return "redirect:/?login=requerido";

        adminPedidoService.cambiarEstado(id, estado);
        return "redirect:/admin";
    }

    @PostMapping("/pedidos/{id}/eliminar")
    public String eliminarPedido(@PathVariable Long id, HttpSession session) {
        if (!adminAuthService.esAdmin(session)) return "redirect:/?login=requerido";

        adminPedidoService.eliminarPedido(id);
        return "redirect:/admin";
    }

}

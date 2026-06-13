package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.Entity.ClienteEntity;

import jakarta.servlet.http.HttpSession;


@Controller
public class maincontroller {
    @GetMapping("/menu")
    public String menu(@RequestParam(required = false, defaultValue = "todos") String categoria, Model model) {
        model.addAttribute("categoria", categoria);
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
    public String nosotros() {
        return "nosotros";
    }
}

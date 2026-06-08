package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class maincontroller {
    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping({"/", "/index.html", "/index"})
    public String index(@RequestParam(required = false) String login,
                        @RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String registro,
                        Model model) {
        if (login != null) {
            model.addAttribute("loginEstado", login);
            model.addAttribute("loginNombre", nombre);
        }
        if (registro != null) {
            model.addAttribute("registroEstado", registro);
        }
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }
}

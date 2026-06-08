package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class maincontroller {
    @GetMapping("/menu")
    public String menu() {
        return "menu";
    }

    @GetMapping({"/", "/index.html", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }
}

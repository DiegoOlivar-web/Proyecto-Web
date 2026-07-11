package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.service.AuthService;
import com.example.Proyecto.service.AuthService.RegistroResultado;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegistroController {

    private final AuthService authService;

    @PostMapping("/registro")
    public String registro(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        RegistroResultado resultado = authService.registrar(cliente);
        if (resultado.estado() == AuthService.RegistroEstado.ERROR) {
            return "redirect:/?registro=error";
        }

        session.setAttribute("usuarioLogueado", resultado.cliente());

        return "redirect:/?registro=exitoso";
    }
}

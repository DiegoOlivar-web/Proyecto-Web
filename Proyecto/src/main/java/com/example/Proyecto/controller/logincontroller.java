package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class logincontroller {
    
    private final ClienteRepository clienteRepository;

    @PostMapping("/login")
    public String login(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente != null && existente.getContrasena().equals(cliente.getContrasena())) {
            session.setAttribute("usuarioLogueado", existente);
            return "redirect:/?login=exitoso";
        }
        return "redirect:/?login=error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout=exitoso";
    }
}

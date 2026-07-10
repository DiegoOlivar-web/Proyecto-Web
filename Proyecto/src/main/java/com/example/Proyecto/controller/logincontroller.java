package com.example.Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class logincontroller {

    @Autowired
    private ClienteRepository clienteRepository ;

    @PostMapping("/login")
    public String login(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente != null
                && !Boolean.FALSE.equals(existente.getActivo())
                && existente.getContrasena().equals(cliente.getContrasena())) {
            // Guardar usuario en sesión
            session.setAttribute("usuarioLogueado", existente);
            // Redirigir al index con parámetro de éxito
            return "redirect:/?login=exitoso";
        }
        // Redirigir al index con parámetro de error
        return "redirect:/?login=error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidar toda la sesión
        session.invalidate();
        // Redirigir al index con parámetro de logout
        return "redirect:/?logout=exitoso";
    }
}


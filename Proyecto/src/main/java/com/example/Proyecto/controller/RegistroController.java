package com.example.Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class RegistroController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/registro")
    public String registro(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente != null) {
            return "redirect:/?registro=error";
        }
        // Guardar nuevo cliente
        cliente.setActivo(true);
        clienteRepository.save(cliente);

        // Guardar en sesión para que el navbar lo reconozca
        session.setAttribute("usuarioLogueado", cliente);

        return "redirect:/?registro=exitoso";
    }
}

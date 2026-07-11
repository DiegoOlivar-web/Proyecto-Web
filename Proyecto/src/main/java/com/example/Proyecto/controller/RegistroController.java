package com.example.Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.service.PasswordService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class RegistroController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/registro")
    public String registro(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente != null) {
            return "redirect:/?registro=error";
        }

        cliente.setActivo(true);
        cliente.setContrasena(passwordService.hash(cliente.getContrasena()));
        ClienteEntity clienteGuardado = clienteRepository.save(cliente);

        session.setAttribute("usuarioLogueado", clienteGuardado);

        return "redirect:/?registro=exitoso";
    }
}

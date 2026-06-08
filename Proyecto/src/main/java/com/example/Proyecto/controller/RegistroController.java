package com.example.Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

@Controller
@RequestMapping("/api")
public class RegistroController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/registro")
    public String registro(@ModelAttribute ClienteEntity cliente) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        if (existente != null) {
            return "redirect:/?registro=error";
        }
        clienteRepository.save(cliente);
        return "redirect:/?registro=exitoso";
    }
}
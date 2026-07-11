package com.example.Proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.service.PasswordService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
public class logincontroller {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/login")
    public String login(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        String contrasenaIngresada = cliente.getContrasena();

        if (existente != null
                && !Boolean.FALSE.equals(existente.getActivo())
                && passwordService.matches(contrasenaIngresada, existente.getContrasena())) {

            if (passwordService.needsRehash(existente.getContrasena())) {
                existente.setContrasena(passwordService.hash(contrasenaIngresada));
                existente = clienteRepository.save(existente);
            }

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

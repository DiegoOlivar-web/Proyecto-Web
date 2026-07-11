package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.service.PasswordService;

import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegistroController {

    private static final int DNI_LENGTH = 8;

    private final ClienteRepository clienteRepository;
    private final PasswordService passwordService;

    @PostMapping("/registro")
    public String registro(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        String correo = limpiar(cliente.getCorreo()).toLowerCase();
        String dni = limpiarDni(cliente.getDni());

        if (correo.isBlank() || dni.length() != DNI_LENGTH || clienteRepository.existsByDni(dni)) {
            return "redirect:/?registro=error";
        }

        ClienteEntity existente = clienteRepository.findByCorreo(correo);
        if (existente != null) {
            return "redirect:/?registro=error";
        }

        cliente.setCorreo(correo);
        cliente.setDni(dni);
        cliente.setNombre(limpiar(cliente.getNombre()));
        cliente.setTelefono(limpiar(cliente.getTelefono()));
        cliente.setDireccion(limpiar(cliente.getDireccion()));
        cliente.setActivo(true);
        cliente.setContrasena(passwordService.hash(cliente.getContrasena()));
        ClienteEntity clienteGuardado = clienteRepository.save(cliente);

        session.setAttribute("usuarioLogueado", clienteGuardado);

        return "redirect:/?registro=exitoso";
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarDni(String dni) {
        return limpiar(dni).replaceAll("\\D", "");
    }
}

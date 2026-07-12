package com.example.Proyecto.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.security.AppUserDetails;
import com.example.Proyecto.service.PasswordService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class RegistroController {

    private final ClienteRepository clientes;
    private final PasswordService passwords;

    @PostMapping("/registro")
    public String registro(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        String correo = limpiar(cliente.getCorreo()).toLowerCase();
        String dni = limpiar(cliente.getDni()).replaceAll("\\D", "");
        if (correo.isBlank() || dni.length() != 8 || clientes.existsByDni(dni) || clientes.findByCorreo(correo).isPresent()) {
            return "redirect:/?registro=error";
        }
        cliente.setCorreo(correo);
        cliente.setDni(dni);
        cliente.setNombre(limpiar(cliente.getNombre()));
        cliente.setTelefono(limpiar(cliente.getTelefono()));
        cliente.setDireccion(limpiar(cliente.getDireccion()));
        cliente.setActivo(true);
        cliente.setRol("CLIENTE");
        cliente.setContrasena(passwords.hash(cliente.getContrasena()));
        ClienteEntity saved = clientes.save(cliente);
        AppUserDetails principal = new AppUserDetails(saved);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        session.setAttribute("usuarioLogueado", saved);
        return "redirect:/?registro=exitoso";
    }

    private String limpiar(String value) {
        return value == null ? "" : value.trim();
    }
}

package com.example.Proyecto.service;

import org.springframework.stereotype.Service;

import com.example.Proyecto.Entity.ClienteEntity;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminAuthService {

    public boolean esAdmin(HttpSession session) {
        ClienteEntity usuario = (ClienteEntity) session.getAttribute("usuarioLogueado");
        return usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol());
    }
}

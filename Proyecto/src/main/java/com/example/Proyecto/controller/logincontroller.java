package com.example.Proyecto.controller;

import org.springframework.web.bind.annotation.*;

import com.example.Proyecto.model.Usuario;

@CrossOrigin(origins = "*") // importante si usas frontend separado
@RestController
@RequestMapping("/api")
public class logincontroller {

    @PostMapping("/login")
    public String login(@RequestBody Usuario usuario) {

        // Usuario de prueba
        if ("admin".equals(usuario.getUsuario()) &&
            "1234".equals(usuario.getPassword())) {
            
            return "Login correcto ✅";
        } else {
            return "Usuario o contraseña incorrectos ❌";
        }
    }
}
package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.service.AuthService;
import com.example.Proyecto.service.AuthService.LoginResultado;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final AuthService authService;

    @PostMapping("/login")
    public String login(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        LoginResultado resultado = authService.login(cliente);
        return switch (resultado.estado()) {
            case EXITO -> {
                session.setAttribute("usuarioLogueado", resultado.cliente());
                if (esAdmin(resultado.cliente())) {
                    yield "redirect:/admin";
                }
                yield "redirect:/?login=exitoso";
            }
            case BLOQUEADO -> "redirect:/?login=bloqueado";
            case ERROR -> "redirect:/?login=error";
        };
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout=exitoso";
    }

    private boolean esAdmin(ClienteEntity cliente) {
        return cliente != null && "ADMIN".equalsIgnoreCase(cliente.getRol());
    }
}

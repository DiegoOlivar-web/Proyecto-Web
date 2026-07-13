package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.security.AppUserDetails;
import com.example.Proyecto.service.AuthService;
import com.example.Proyecto.service.AuthService.LoginResultado;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    public String login(@ModelAttribute ClienteEntity cliente, HttpSession session,
            HttpServletRequest request, HttpServletResponse response) {
        LoginResultado resultado = authService.login(cliente);
        return switch (resultado.estado()) {
            case EXITO -> {
                session.setAttribute("usuarioLogueado", resultado.cliente());
                autenticarEnSpringSecurity(resultado.cliente(), request, response);
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
        SecurityContextHolder.clearContext();
        return "redirect:/?logout=exitoso";
    }

    private boolean esAdmin(ClienteEntity cliente) {
        return cliente != null && "ADMIN".equalsIgnoreCase(cliente.getRol());
    }

    private void autenticarEnSpringSecurity(ClienteEntity cliente, HttpServletRequest request, HttpServletResponse response) {
        AppUserDetails userDetails = new AppUserDetails(cliente);
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
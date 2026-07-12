package com.example.Proyecto.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Proyecto.security.AppUserDetails;
import com.example.Proyecto.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody LoginRequest request) {
        try {
            var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.correo(), request.contrasena()));
            AppUserDetails user = (AppUserDetails) auth.getPrincipal();
            return ResponseEntity.ok(Map.of("token", jwtService.generate(user), "tipo", "Bearer", "rol", user.getAuthorities().iterator().next().getAuthority()));
        } catch (Exception ex) { return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas")); }
    }
    public record LoginRequest(String correo, String contrasena) { }
}

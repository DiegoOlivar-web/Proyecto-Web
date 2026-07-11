package com.example.Proyecto.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

public class logincontroller {

    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private static final int SEGUNDOS_BLOQUEO = 10;

    private final ClienteRepository clienteRepository;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public String login(@ModelAttribute ClienteEntity cliente, HttpSession session) {
        ClienteEntity existente = clienteRepository.findByCorreo(cliente.getCorreo());
        String contrasenaIngresada = cliente.getContrasena();
        LocalDateTime ahora = LocalDateTime.now();

        if (existente == null || Boolean.FALSE.equals(existente.getActivo())) {
            return "redirect:/?login=error";
        }

        if (estaBloqueado(existente, ahora)) {
            return "redirect:/?login=bloqueado";
        }

        if (bloqueoExpiro(existente, ahora)) {
            limpiarBloqueo(existente);
            existente = clienteRepository.save(existente);
        }

        if (!passwordService.matches(contrasenaIngresada, existente.getContrasena())) {
            return registrarIntentoFallido(existente, ahora);
        }

        limpiarBloqueo(existente);
        if (passwordService.needsRehash(existente.getContrasena())) {
            existente.setContrasena(passwordService.hash(contrasenaIngresada));
        }

        existente = clienteRepository.save(existente);
        session.setAttribute("usuarioLogueado", existente);
        return "redirect:/?login=exitoso";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout=exitoso";
    }

    private boolean estaBloqueado(ClienteEntity cliente, LocalDateTime ahora) {
        return cliente.getBloqueadoHasta() != null && cliente.getBloqueadoHasta().isAfter(ahora);
    }

    private boolean bloqueoExpiro(ClienteEntity cliente, LocalDateTime ahora) {
        return cliente.getBloqueadoHasta() != null && !cliente.getBloqueadoHasta().isAfter(ahora);
    }

    private String registrarIntentoFallido(ClienteEntity cliente, LocalDateTime ahora) {
        int intentos = cliente.getIntentosFallidos() == null ? 0 : cliente.getIntentosFallidos();
        intentos++;
        cliente.setIntentosFallidos(intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            cliente.setBloqueadoHasta(ahora.plusSeconds(SEGUNDOS_BLOQUEO));
            clienteRepository.save(cliente);
            return "redirect:/?login=bloqueado";
        }

        clienteRepository.save(cliente);
        return "redirect:/?login=error";
    }

    private void limpiarBloqueo(ClienteEntity cliente) {
        cliente.setIntentosFallidos(0);
        cliente.setBloqueadoHasta(null);
    }
}

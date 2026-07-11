package com.example.Proyecto.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int DNI_LENGTH = 8;
    private static final int MAX_INTENTOS_FALLIDOS = 3;
    private static final int SEGUNDOS_BLOQUEO = 10;

    private final ClienteRepository clienteRepository;
    private final PasswordService passwordService;

    public LoginResultado login(ClienteEntity credenciales) {
        ClienteEntity existente = clienteRepository.findByCorreo(credenciales.getCorreo());
        String contrasenaIngresada = credenciales.getContrasena();
        LocalDateTime ahora = LocalDateTime.now();

        if (existente == null || Boolean.FALSE.equals(existente.getActivo())) {
            return new LoginResultado(LoginEstado.ERROR, null);
        }

        if (estaBloqueado(existente, ahora)) {
            return new LoginResultado(LoginEstado.BLOQUEADO, null);
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
        return new LoginResultado(LoginEstado.EXITO, existente);
    }

    public RegistroResultado registrar(ClienteEntity cliente) {
        String correo = limpiar(cliente.getCorreo()).toLowerCase();
        String dni = limpiarDni(cliente.getDni());

        if (correo.isBlank() || dni.length() != DNI_LENGTH || clienteRepository.existsByDni(dni)) {
            return new RegistroResultado(RegistroEstado.ERROR, null);
        }

        ClienteEntity existente = clienteRepository.findByCorreo(correo);
        if (existente != null) {
            return new RegistroResultado(RegistroEstado.ERROR, null);
        }

        cliente.setCorreo(correo);
        cliente.setDni(dni);
        cliente.setNombre(limpiar(cliente.getNombre()));
        cliente.setTelefono(limpiar(cliente.getTelefono()));
        cliente.setDireccion(limpiar(cliente.getDireccion()));
        cliente.setActivo(true);
        cliente.setContrasena(passwordService.hash(cliente.getContrasena()));

        ClienteEntity clienteGuardado = clienteRepository.save(cliente);
        return new RegistroResultado(RegistroEstado.EXITO, clienteGuardado);
    }

    private LoginResultado registrarIntentoFallido(ClienteEntity cliente, LocalDateTime ahora) {
        int intentos = cliente.getIntentosFallidos() == null ? 0 : cliente.getIntentosFallidos();
        intentos++;
        cliente.setIntentosFallidos(intentos);

        if (intentos >= MAX_INTENTOS_FALLIDOS) {
            cliente.setBloqueadoHasta(ahora.plusSeconds(SEGUNDOS_BLOQUEO));
            clienteRepository.save(cliente);
            return new LoginResultado(LoginEstado.BLOQUEADO, null);
        }

        clienteRepository.save(cliente);
        return new LoginResultado(LoginEstado.ERROR, null);
    }

    private boolean estaBloqueado(ClienteEntity cliente, LocalDateTime ahora) {
        return cliente.getBloqueadoHasta() != null && cliente.getBloqueadoHasta().isAfter(ahora);
    }

    private boolean bloqueoExpiro(ClienteEntity cliente, LocalDateTime ahora) {
        return cliente.getBloqueadoHasta() != null && !cliente.getBloqueadoHasta().isAfter(ahora);
    }

    private void limpiarBloqueo(ClienteEntity cliente) {
        cliente.setIntentosFallidos(0);
        cliente.setBloqueadoHasta(null);
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarDni(String dni) {
        return limpiar(dni).replaceAll("\\D", "");
    }

    public enum LoginEstado {
        EXITO,
        ERROR,
        BLOQUEADO
    }

    public enum RegistroEstado {
        EXITO,
        ERROR
    }

    public record LoginResultado(LoginEstado estado, ClienteEntity cliente) {
    }

    public record RegistroResultado(RegistroEstado estado, ClienteEntity cliente) {
    }
}

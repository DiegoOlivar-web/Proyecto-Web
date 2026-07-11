package com.example.Proyecto.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final ClienteRepository clienteRepository;
    private final PasswordService passwordService;

    public Optional<ClienteEntity> obtenerUsuarioActivo(ClienteEntity usuarioSesion) {
        if (usuarioSesion == null || usuarioSesion.getId() == null) {
            return Optional.empty();
        }

        Optional<ClienteEntity> usuario = clienteRepository.findById(usuarioSesion.getId());
        if (usuario.isEmpty() || Boolean.FALSE.equals(usuario.get().getActivo())) {
            return Optional.empty();
        }

        return usuario;
    }

    public ClienteEntity actualizarPerfil(
            ClienteEntity usuario,
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String direccion,
            String distrito,
            String referencia) {

        usuario.setNombre(nombre);
        usuario.setApellido(limpiar(apellido));
        usuario.setDni(limpiar(dni));
        usuario.setTelefono(limpiar(telefono));
        usuario.setDireccion(direccion);
        usuario.setDistrito(limpiar(distrito));
        usuario.setReferencia(limpiar(referencia));

        return clienteRepository.save(usuario);
    }

    public PasswordResultado cambiarPassword(
            ClienteEntity usuario,
            String contrasenaActual,
            String nuevaContrasena,
            String confirmarContrasena) {

        if (!passwordService.matches(contrasenaActual, usuario.getContrasena())) {
            return new PasswordResultado(PasswordEstado.ACTUAL_INVALIDA, null);
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            return new PasswordResultado(PasswordEstado.CONFIRMACION_INVALIDA, null);
        }

        usuario.setContrasena(passwordService.hash(nuevaContrasena));
        return new PasswordResultado(PasswordEstado.EXITO, clienteRepository.save(usuario));
    }

    public void desactivarCuenta(ClienteEntity usuario) {
        usuario.setActivo(false);
        clienteRepository.save(usuario);
    }

    private String limpiar(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    public enum PasswordEstado {
        EXITO,
        ACTUAL_INVALIDA,
        CONFIRMACION_INVALIDA
    }

    public record PasswordResultado(PasswordEstado estado, ClienteEntity cliente) {
    }
}

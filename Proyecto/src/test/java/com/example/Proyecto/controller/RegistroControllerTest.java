package com.example.Proyecto.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.service.PasswordService;

class RegistroControllerTest {

    @Test
    void guardaDniNormalizadoAlRegistrarCliente() {
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNombre(" Andres ");
        cliente.setCorreo("ANDRES@TEST.COM ");
        cliente.setDni("1234 5678");
        cliente.setTelefono(" 987654321 ");
        cliente.setDireccion(" Av. Peru 123 ");
        cliente.setContrasena("secreta");

        when(clienteRepository.existsByDni("12345678")).thenReturn(false);
        when(clienteRepository.findByCorreo("andres@test.com")).thenReturn(null);
        when(passwordService.hash("secreta")).thenReturn("hash");
        when(clienteRepository.save(any(ClienteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegistroController controller = new RegistroController(clienteRepository, passwordService);
        MockHttpSession session = new MockHttpSession();

        String respuesta = controller.registro(cliente, session);

        assertEquals("redirect:/?registro=exitoso", respuesta);
        assertEquals("12345678", cliente.getDni());
        assertEquals("andres@test.com", cliente.getCorreo());
        assertEquals("hash", cliente.getContrasena());
        assertSame(cliente, session.getAttribute("usuarioLogueado"));
    }

    @Test
    void rechazaRegistroConDniInvalido() {
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        ClienteEntity cliente = new ClienteEntity();
        cliente.setCorreo("andres@test.com");
        cliente.setDni("123");

        RegistroController controller = new RegistroController(clienteRepository, passwordService);

        String respuesta = controller.registro(cliente, new MockHttpSession());

        assertEquals("redirect:/?registro=error", respuesta);
        verify(clienteRepository, never()).save(any(ClienteEntity.class));
    }
}

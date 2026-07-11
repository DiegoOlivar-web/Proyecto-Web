package com.example.Proyecto.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.service.AuthService;
import com.example.Proyecto.service.PasswordService;

class LoginControllerTest {

    @Test
    void bloqueaCuentaDespuesDeTresIntentosFallidos() {
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        PasswordService passwordService = mock(PasswordService.class);
        ClienteEntity usuario = new ClienteEntity();
        usuario.setCorreo("andres@test.com");
        usuario.setContrasena("hash");
        usuario.setActivo(true);
        usuario.setIntentosFallidos(0);

        ClienteEntity intento = new ClienteEntity();
        intento.setCorreo("andres@test.com");
        intento.setContrasena("incorrecta");

        when(clienteRepository.findByCorreo("andres@test.com")).thenReturn(usuario);
        when(passwordService.matches("incorrecta", "hash")).thenReturn(false);
        when(clienteRepository.save(usuario)).thenReturn(usuario);

        AuthService authService = new AuthService(clienteRepository, passwordService);
        logincontroller controller = new logincontroller(authService);
        MockHttpSession session = new MockHttpSession();

        assertEquals("redirect:/?login=error", controller.login(intento, session));
        assertEquals("redirect:/?login=error", controller.login(intento, session));
        assertEquals("redirect:/?login=bloqueado", controller.login(intento, session));
        assertEquals(3, usuario.getIntentosFallidos());
        assertNotNull(usuario.getBloqueadoHasta());
    }
}

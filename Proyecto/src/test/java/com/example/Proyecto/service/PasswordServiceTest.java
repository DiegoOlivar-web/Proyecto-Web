package com.example.Proyecto.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordServiceTest {

    private final PasswordService passwordService = new PasswordService();

    @Test
    void hashProtegeYValidaPassword() {
        String hash = passwordService.hash("clave123");

        assertTrue(hash.startsWith("pbkdf2$"));
        assertNotEquals("clave123", hash);
        assertTrue(passwordService.matches("clave123", hash));
        assertFalse(passwordService.matches("otraClave", hash));
    }

    @Test
    void aceptaPasswordsAntiguasEnTextoPlanoParaMigracion() {
        assertTrue(passwordService.matches("admin123", "admin123"));
        assertTrue(passwordService.needsRehash("admin123"));
    }
}

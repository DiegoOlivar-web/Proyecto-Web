package com.example.Proyecto.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.Proyecto.Entity.ClienteEntity;

public record AppUserDetails(ClienteEntity cliente) implements UserDetails {
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        String rol = cliente.getRol() == null || cliente.getRol().isBlank() ? "CLIENTE" : cliente.getRol();
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()));
    }
    @Override public String getPassword() { return cliente.getContrasena(); }
    @Override public String getUsername() { return cliente.getCorreo(); }
    @Override public boolean isEnabled() { return !Boolean.FALSE.equals(cliente.getActivo()); }
    @Override public boolean isAccountNonLocked() { return cliente.getBloqueadoHasta() == null || !cliente.getBloqueadoHasta().isAfter(java.time.LocalDateTime.now()); }
}

package com.example.Proyecto.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Proyecto.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteUserDetailsService implements UserDetailsService {
    private final ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) {
        return clienteRepository.findByCorreo(correo.trim().toLowerCase())
            .map(AppUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
    }
}

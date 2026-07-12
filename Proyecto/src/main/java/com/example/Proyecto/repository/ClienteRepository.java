package com.example.Proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Entity.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByCorreo(String correo);
    boolean existsByDni(String dni);
}

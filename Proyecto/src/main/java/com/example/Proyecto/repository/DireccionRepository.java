package com.example.Proyecto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Entity.DireccionEntity;

public interface DireccionRepository extends JpaRepository<DireccionEntity, Long> {
    Optional<DireccionEntity> findFirstByCliente_IdOrderByIdAsc(Long clienteId);
}

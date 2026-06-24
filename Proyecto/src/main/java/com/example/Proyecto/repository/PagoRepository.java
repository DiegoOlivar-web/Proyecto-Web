package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Entity.PagoEntity;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {
}

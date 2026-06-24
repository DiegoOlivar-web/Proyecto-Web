package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Entity.ComprobanteEntity;

public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, Long> {
}

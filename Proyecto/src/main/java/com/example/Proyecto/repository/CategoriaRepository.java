package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Proyecto.Entity.CategoriaEntity;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
}

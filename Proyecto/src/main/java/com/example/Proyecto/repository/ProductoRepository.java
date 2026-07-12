package com.example.Proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Entity.ProductosEntity;

public interface ProductoRepository
        extends JpaRepository<ProductosEntity, Long> {

    @Query("""
        SELECT p
        FROM ProductosEntity p
        LEFT JOIN FETCH p.categoria
        WHERE p.disponible = true OR p.disponible IS NULL
        ORDER BY p.id
    """)
    List<ProductosEntity> findDisponiblesConCategoria();

    @Query("""
        SELECT p
        FROM ProductosEntity p
        LEFT JOIN FETCH p.categoria
        ORDER BY p.id
    """)
    List<ProductosEntity> findAllConCategoria();
}
package com.example.Proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Proyecto.Entity.ProductosEntity;

public interface ProductoRepository extends JpaRepository<ProductosEntity, Long> {

    List<ProductosEntity> findByCategoria_Id(Long categoriaId);

    @Query("select p from ProductosEntity p left join fetch p.categoria where (p.disponible = true or p.disponible is null) order by p.id")
    List<ProductosEntity> findDisponiblesConCategoria();

    @Query("select p from ProductosEntity p left join fetch p.categoria order by p.id desc")
    List<ProductosEntity> findAllConCategoria();
}

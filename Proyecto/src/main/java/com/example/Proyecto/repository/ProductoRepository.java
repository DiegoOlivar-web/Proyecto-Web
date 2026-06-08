package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Proyecto.Entity.ProductosEntity;
import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductosEntity, Long> {
    List<ProductosEntity> findByCategoriaId(Integer categoriaId);
}

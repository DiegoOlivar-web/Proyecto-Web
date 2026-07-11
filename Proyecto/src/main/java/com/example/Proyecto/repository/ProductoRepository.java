package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.Proyecto.Entity.ProductosEntity;
import java.util.List;

public interface ProductoRepository extends JpaRepository<ProductosEntity, Long> {
    List<ProductosEntity> findByCategoria_Id(Long categoriaId);

    @Query("select p from ProductosEntity p left join fetch p.categoria where (p.disponible = true or p.disponible is null) order by p.id")
    List<ProductosEntity> findDisponiblesConCategoria();
}

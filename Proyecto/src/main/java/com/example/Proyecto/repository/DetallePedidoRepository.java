package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Proyecto.Entity.DetallePedidoEntity;

public interface DetallePedidoRepository extends JpaRepository<DetallePedidoEntity, Long> {
}
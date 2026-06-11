package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Proyecto.Entity.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
}
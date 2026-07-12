package com.example.Proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.Proyecto.Entity.DetallePedidoEntity;

public interface DetallePedidoRepository extends JpaRepository<DetallePedidoEntity, Long> {
    @Query("select d from DetallePedidoEntity d join fetch d.producto where d.pedido.id = :pedidoId")
    List<DetallePedidoEntity> findByPedidoIdConProducto(@Param("pedidoId") Long pedidoId);
}

package com.example.Proyecto.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.Proyecto.Entity.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    @Query("select distinct p from PedidoEntity p join fetch p.cliente left join fetch p.direccion left join fetch p.comprobante order by p.fechaPedido desc")
    List<PedidoEntity> findAllConClienteDireccion();
}

package com.example.Proyecto.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detalle_pedido")
@Data
public class DetallePedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private PedidoEntity pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private ProductosEntity producto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;
}

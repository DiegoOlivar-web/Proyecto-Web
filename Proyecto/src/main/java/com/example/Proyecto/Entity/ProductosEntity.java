package com.example.Proyecto.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "producto")   // asegura que se mapea a la tabla correcta
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private double precio;

    @Column(name = "categoria_id")   // asegura que se mapea al campo correcto
    private Integer categoriaId;

    private Boolean disponible = true;
}

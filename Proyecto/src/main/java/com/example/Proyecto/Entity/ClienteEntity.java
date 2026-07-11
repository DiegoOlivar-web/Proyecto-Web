package com.example.Proyecto.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    private String dni;

    private String telefono;

    private String correo;

    private String contrasena;

    private String direccion;

    private String distrito;

    private String referencia;

    private Boolean activo;

    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @PrePersist
    public void prePersist() {
        if (activo == null) {
            activo = true;
        }
        if (intentosFallidos == null) {
            intentosFallidos = 0;
        }
    }
}

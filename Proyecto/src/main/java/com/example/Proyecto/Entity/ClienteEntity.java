package com.example.Proyecto.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Data @NoArgsConstructor @AllArgsConstructor
public class ClienteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nombre, apellido, dni, telefono, correo, contrasena, direccion, distrito, referencia;
    private Boolean activo;
    @Column(name = "rol") private String rol;
    @Column(name = "intentos_fallidos") private Integer intentosFallidos;
    @Column(name = "bloqueado_hasta") private LocalDateTime bloqueadoHasta;
    @PrePersist public void prePersist() { if (activo == null) activo = true; if (rol == null || rol.isBlank()) rol = "CLIENTE"; if (intentosFallidos == null) intentosFallidos = 0; }
}

package com.example.Proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Proyecto.Entity.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    ClienteEntity findByCorreo(String correo);

    boolean existsByDni(String dni);
}

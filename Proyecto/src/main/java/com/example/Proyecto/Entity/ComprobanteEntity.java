package com.example.Proyecto.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comprobante")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    private String tipo;

    @Column(name = "numero_documento")
    private String numeroDocumento;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "direccion_fiscal")
    private String direccionFiscal;
}

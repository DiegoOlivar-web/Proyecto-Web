package com.example.Proyecto.model;

import lombok.Data;

@Data
public class CheckoutRequest {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String tipoEntrega = "DELIVERY";
    private String direccion;
    private String distrito;
    private String ciudad;
    private String referencia;
    private String indicaciones;
    private String tipoComprobante = "BOLETA";
    private String numeroDocumento;
    private String razonSocial;
    private String direccionFiscal;
    private String metodoPago;
}

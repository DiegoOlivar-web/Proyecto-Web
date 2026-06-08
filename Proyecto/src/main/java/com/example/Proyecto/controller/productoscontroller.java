package com.example.Proyecto.controller;

import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.repository.ProductoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class productoscontroller {

    private final ProductoRepository productoRepository;

    public productoscontroller(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Listar todos los productos
    @GetMapping
    public List<ProductosEntity> listarTodos() {
        return productoRepository.findAll();
    }

    // Listar productos por categoría
    @GetMapping("/categoria/{id}")
    public List<ProductosEntity> listarPorCategoria(@PathVariable Integer id) {
        return productoRepository.findByCategoriaId(id);
    }

    // Agregar un producto nuevo
    @PostMapping
    public ProductosEntity agregar(@RequestBody ProductosEntity producto) {
        return productoRepository.save(producto);
    }
}


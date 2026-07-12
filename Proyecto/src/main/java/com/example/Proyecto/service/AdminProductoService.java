package com.example.Proyecto.service;

import java.io.IOException;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto.Entity.CategoriaEntity;
import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.repository.CategoriaRepository;
import com.example.Proyecto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FileStorageService fileStorageService;

    public List<ProductosEntity> listarProductos() {
        return productoRepository.findAllConCategoria();
    }

    public List<CategoriaEntity> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public void guardar(Long id, String nombre, String descripcion, double precio, Long categoriaId,
            boolean disponible, MultipartFile imagen) throws IOException {
        ProductosEntity producto = id == null ? new ProductosEntity() : productoRepository.findById(id).orElseThrow();

        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion.trim());
        producto.setPrecio(precio);
        producto.setDisponible(disponible);
        producto.setCategoria(categoriaRepository.findById(categoriaId).orElseThrow());

        if (imagen != null && !imagen.isEmpty()) {
            producto.setImagen(fileStorageService.guardarImagen(imagen));
        }

        productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            try {
                productoRepository.delete(producto);
            } catch (DataIntegrityViolationException ex) {
                producto.setDisponible(false);
                productoRepository.save(producto);
            }
        });
    }
}

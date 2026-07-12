package com.example.Proyecto.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.repository.CategoriaRepository;
import com.example.Proyecto.repository.DetallePedidoRepository;
import com.example.Proyecto.repository.PedidoRepository;
import com.example.Proyecto.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PedidoRepository pedidos;
    private final DetallePedidoRepository detalles;
    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    @GetMapping
    @Transactional(readOnly = true)
    public String verPedidos(Model model) {
        var lista = pedidos.findAllConClienteDireccion();
        var detallePorPedido = lista.stream().collect(java.util.stream.Collectors.toMap(
                pedido -> pedido.getId(), pedido -> detalles.findByPedidoIdConProducto(pedido.getId())));
        model.addAttribute("pedidos", lista);
        model.addAttribute("detallesPorPedido", detallePorPedido);
        model.addAttribute("pendientes", lista.stream().filter(p -> !"ENTREGADO".equals(p.getEstado())).count());
        model.addAttribute("entregados", lista.stream().filter(p -> "ENTREGADO".equals(p.getEstado())).count());
        return "pedidosAdmin"; // templates/pedidosAdmin.html
    }

    @PostMapping("/pedidos/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        if (!"PENDIENTE".equals(estado) && !"ENTREGADO".equals(estado)) {
            return "redirect:/admin";
        }

        pedidos.findById(id).ifPresent(pedido -> {
            pedido.setEstado(estado);
            pedidos.save(pedido);
        });
        return "redirect:/admin";
    }

    @PostMapping("/pedidos/{id}/eliminar")
    public String eliminarPedido(@PathVariable Long id) {
        pedidos.findById(id).ifPresent(pedido -> {
            pedido.setArchivado(true);
            pedidos.save(pedido);
        });
        return "redirect:/admin";
    }

    @GetMapping("/menu")
    public String verMenu(@RequestParam(required = false) Long categoriaId, Model model) {
        var lista = productos.findAllConCategoria();
        if (categoriaId != null) {
            lista = lista.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().getId().equals(categoriaId))
                    .toList();
        }
        model.addAttribute("productos", lista);
        model.addAttribute("categorias", categorias.findAll());
        model.addAttribute("categoriaSeleccionada", categoriaId);
        return "menuAdmin";
    }

    @PostMapping("/menu")
    public String guardarProducto(@RequestParam(required = false) Long id, @RequestParam String nombre,
            @RequestParam String descripcion, @RequestParam double precio, @RequestParam Long categoriaId,
            @RequestParam(defaultValue = "false") boolean disponible, @RequestParam(required = false) MultipartFile imagen) throws IOException {
        ProductosEntity producto = id == null ? new ProductosEntity() : productos.findById(id).orElseThrow();
        producto.setNombre(nombre.trim());
        producto.setDescripcion(descripcion.trim());
        producto.setPrecio(precio);
        producto.setDisponible(disponible);
        producto.setCategoria(categorias.findById(categoriaId).orElseThrow());
        if (imagen != null && !imagen.isEmpty()) {
            producto.setImagen(guardarImagen(imagen));
        }
        productos.save(producto);
        return "redirect:/admin/menu";
    }

    @PostMapping("/menu/{id}/eliminar")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productos.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorProducto",
                    "No se puede eliminar: este producto ya tiene pedidos registrados. Márcalo como 'No disponible' en su lugar.");
        }
        return "redirect:/admin/menu";
    }

    private String guardarImagen(MultipartFile imagen) throws IOException {
        if (imagen.getContentType() == null || !imagen.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten imágenes");
        }
        String nombreOriginal = imagen.getOriginalFilename();
        String extension = nombreOriginal != null && nombreOriginal.lastIndexOf('.') >= 0
                ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.')) : ".jpg";
        Path carpeta = Path.of("uploads").toAbsolutePath().normalize();
        Files.createDirectories(carpeta);
        String archivo = UUID.randomUUID() + extension.toLowerCase();
        Files.copy(imagen.getInputStream(), carpeta.resolve(archivo), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + archivo;
    }
}

package com.example.Proyecto.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    public String guardarImagen(MultipartFile imagen) throws IOException {
        if (imagen.getContentType() == null || !imagen.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten imagenes");
        }

        String nombreOriginal = imagen.getOriginalFilename();
        String extension = nombreOriginal != null && nombreOriginal.lastIndexOf('.') >= 0
                ? nombreOriginal.substring(nombreOriginal.lastIndexOf('.'))
                : ".jpg";

        Path carpeta = Path.of("uploads").toAbsolutePath().normalize();
        Files.createDirectories(carpeta);

        String archivo = UUID.randomUUID() + extension.toLowerCase();
        Files.copy(imagen.getInputStream(), carpeta.resolve(archivo), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + archivo;
    }
}

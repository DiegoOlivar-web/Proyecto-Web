package com.example.Proyecto.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.repository.ClienteRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PerfilController {

    private final ClienteRepository clienteRepository;

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        ClienteEntity usuario = obtenerUsuarioActual(session);
        if (usuario == null) {
            return "redirect:/?login=requerido";
        }

        model.addAttribute("perfil", usuario);
        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono,
            @RequestParam String direccion,
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) String referencia,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        ClienteEntity usuario = obtenerUsuarioActual(session);
        if (usuario == null) {
            return "redirect:/?login=requerido";
        }

        usuario.setNombre(nombre);
        usuario.setApellido(limpiar(apellido));
        usuario.setDni(limpiar(dni));
        usuario.setTelefono(limpiar(telefono));
        usuario.setDireccion(direccion);
        usuario.setDistrito(limpiar(distrito));
        usuario.setReferencia(limpiar(referencia));

        ClienteEntity actualizado = clienteRepository.save(usuario);
        session.setAttribute("usuarioLogueado", actualizado);
        redirectAttributes.addFlashAttribute("perfilExito", "Tus datos fueron actualizados.");

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/password")
    public String cambiarPassword(
            @RequestParam String contrasenaActual,
            @RequestParam String nuevaContrasena,
            @RequestParam String confirmarContrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        ClienteEntity usuario = obtenerUsuarioActual(session);
        if (usuario == null) {
            return "redirect:/?login=requerido";
        }

        if (!usuario.getContrasena().equals(contrasenaActual)) {
            redirectAttributes.addFlashAttribute("perfilError", "La contraseña actual no coincide.");
            return "redirect:/perfil";
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            redirectAttributes.addFlashAttribute("perfilError", "La nueva contraseña y la confirmación no coinciden.");
            return "redirect:/perfil";
        }

        usuario.setContrasena(nuevaContrasena);
        ClienteEntity actualizado = clienteRepository.save(usuario);
        session.setAttribute("usuarioLogueado", actualizado);
        redirectAttributes.addFlashAttribute("perfilExito", "Tu contraseña fue actualizada.");

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(HttpSession session, RedirectAttributes redirectAttributes) {
        ClienteEntity usuario = obtenerUsuarioActual(session);
        if (usuario == null) {
            return "redirect:/?login=requerido";
        }

        usuario.setActivo(false);
        clienteRepository.save(usuario);
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutEstado", "cuenta_eliminada");

        return "redirect:/";
    }

    private ClienteEntity obtenerUsuarioActual(HttpSession session) {
        ClienteEntity usuarioSesion = (ClienteEntity) session.getAttribute("usuarioLogueado");
        if (usuarioSesion == null || usuarioSesion.getId() == null) {
            return null;
        }

        Optional<ClienteEntity> usuario = clienteRepository.findById(usuarioSesion.getId());
        if (usuario.isEmpty() || Boolean.FALSE.equals(usuario.get().getActivo())) {
            session.removeAttribute("usuarioLogueado");
            return null;
        }

        return usuario.get();
    }

    private String limpiar(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }
}

package com.example.Proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.service.PerfilService;
import com.example.Proyecto.service.PerfilService.PasswordResultado;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        ClienteEntity usuario = obtenerUsuarioActual(session);
        if (usuario == null) {
            return "redirect:/?login=requerido";
        }

        model.addAttribute("perfil", usuario);
        model.addAttribute("perfilNombre", valorFormulario(usuario.getNombre()));
        model.addAttribute("perfilApellido", valorFormulario(usuario.getApellido()));
        model.addAttribute("perfilDni", valorFormulario(usuario.getDni()));
        model.addAttribute("perfilTelefono", valorFormulario(usuario.getTelefono()));
        model.addAttribute("perfilDireccion", valorFormulario(usuario.getDireccion()));
        model.addAttribute("perfilDistrito", valorFormulario(usuario.getDistrito()));
        model.addAttribute("perfilReferencia", valorFormulario(usuario.getReferencia()));
        model.addAttribute("perfilResumenDni", valorResumen(usuario.getDni()));
        model.addAttribute("perfilResumenTelefono", valorResumen(usuario.getTelefono()));
        model.addAttribute("perfilResumenDireccion", valorResumen(usuario.getDireccion()));
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

        ClienteEntity actualizado = perfilService.actualizarPerfil(
                usuario,
                nombre,
                apellido,
                dni,
                telefono,
                direccion,
                distrito,
                referencia);

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

        PasswordResultado resultado = perfilService.cambiarPassword(
                usuario,
                contrasenaActual,
                nuevaContrasena,
                confirmarContrasena);

        return switch (resultado.estado()) {
            case ACTUAL_INVALIDA -> {
                redirectAttributes.addFlashAttribute("perfilError", "La contrasena actual no coincide.");
                yield "redirect:/perfil";
            }
            case CONFIRMACION_INVALIDA -> {
                redirectAttributes.addFlashAttribute("perfilError", "La nueva contrasena y la confirmacion no coinciden.");
                yield "redirect:/perfil";
            }
            case EXITO -> {
                session.setAttribute("usuarioLogueado", resultado.cliente());
                redirectAttributes.addFlashAttribute("perfilExito", "Tu contrasena fue actualizada.");
                yield "redirect:/perfil";
            }
        };
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(HttpSession session) {
        ClienteEntity usuario = obtenerUsuarioActual(session);
        if (usuario == null) {
            return "redirect:/?login=requerido";
        }

        perfilService.desactivarCuenta(usuario);
        session.invalidate();
        return "redirect:/?logout=cuenta_eliminada";
    }

    private ClienteEntity obtenerUsuarioActual(HttpSession session) {
        ClienteEntity usuarioSesion = (ClienteEntity) session.getAttribute("usuarioLogueado");
        return perfilService.obtenerUsuarioActivo(usuarioSesion)
                .orElseGet(() -> {
                    session.removeAttribute("usuarioLogueado");
                    return null;
                });
    }

    private String valorFormulario(String valor) {
        return valor == null ? "" : valor;
    }

    private String valorResumen(String valor) {
        return valor == null || valor.trim().isEmpty() ? "Pendiente" : valor;
    }
}

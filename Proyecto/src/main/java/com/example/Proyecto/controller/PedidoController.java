package com.example.Proyecto.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto.Entity.ClienteEntity;
import com.example.Proyecto.Entity.ComprobanteEntity;
import com.example.Proyecto.Entity.DetallePedidoEntity;
import com.example.Proyecto.Entity.DireccionEntity;
import com.example.Proyecto.Entity.PagoEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.Entity.ProductosEntity;
import com.example.Proyecto.model.CartItem;
import com.example.Proyecto.repository.ClienteRepository;
import com.example.Proyecto.repository.ComprobanteRepository;
import com.example.Proyecto.repository.DetallePedidoRepository;
import com.example.Proyecto.repository.DireccionRepository;
import com.example.Proyecto.repository.PagoRepository;
import com.example.Proyecto.repository.PedidoRepository;
import com.example.Proyecto.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PedidoController {

    private static final String ENTREGA_DELIVERY = "DELIVERY";
    private static final String ENTREGA_RECOJO = "RECOJO";
    private static final String COMPROBANTE_BOLETA = "BOLETA";
    private static final String TIENDA_DIRECCION = "Ca. Jose Galvez 390, Lima 15074";
    private static final String CIUDAD_DEFAULT = "Lima";

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final DireccionRepository direccionRepository;
    private final ClienteRepository clienteRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final PagoRepository pagoRepository;

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        ClienteEntity usuarioLogueado = obtenerUsuarioLogueado(session);

        if (usuarioLogueado == null) {
            return "redirect:/?login=requerido";
        }

        List<CartItem> cart = obtenerCarrito(session);
        if (cart.isEmpty()) {
            return "redirect:/menu";
        }

        model.addAttribute("cliente", usuarioLogueado);
        model.addAttribute("checkoutSubtotal", calcularTotal(cart));
        model.addAttribute("tiendaDireccion", TIENDA_DIRECCION);

        return "checkout";
    }

    @PostMapping("/cart/confirmar")
    public String redirigirCheckout(HttpSession session) {
        if (obtenerUsuarioLogueado(session) == null) {
            return "redirect:/?login=requerido";
        }

        if (obtenerCarrito(session).isEmpty()) {
            return "redirect:/menu";
        }

        return "redirect:/checkout";
    }

    @PostMapping("/checkout/confirmar")
    @Transactional
    public String procesarPedido(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String telefono,
            @RequestParam(defaultValue = ENTREGA_DELIVERY) String tipoEntrega,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String distrito,
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String referencia,
            @RequestParam(required = false) String indicaciones,
            @RequestParam(defaultValue = COMPROBANTE_BOLETA) String tipoComprobante,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) String razonSocial,
            @RequestParam(required = false) String direccionFiscal,
            @RequestParam String metodoPago,
            HttpSession session) {

        ClienteEntity usuarioLogueado = obtenerUsuarioLogueado(session);
        if (usuarioLogueado == null) {
            return "redirect:/?login=requerido";
        }

        List<CartItem> cart = obtenerCarrito(session);
        if (cart.isEmpty()) {
            return "redirect:/menu";
        }

        ClienteEntity cliente = actualizarCliente(
                usuarioLogueado,
                nombre,
                apellido,
                dni,
                telefono,
                tipoEntrega,
                direccion,
                distrito,
                referencia,
                indicaciones);

        DireccionEntity direccionPedido = guardarDireccion(
                cliente,
                tipoEntrega,
                direccion,
                distrito,
                ciudad,
                referencia,
                indicaciones);

        ComprobanteEntity comprobante = guardarComprobante(
                cliente,
                tipoComprobante,
                numeroDocumento,
                razonSocial,
                direccionFiscal,
                direccionPedido);

        PedidoEntity pedidoGuardado = guardarPedido(cliente, direccionPedido, comprobante, cart);
        guardarDetallesPedido(pedidoGuardado, cart);
        guardarPago(pedidoGuardado, metodoPago);

        session.setAttribute("usuarioLogueado", cliente);
        session.removeAttribute("cart");

        return "redirect:/?pedido=exitoso";
    }

    private ClienteEntity actualizarCliente(
            ClienteEntity cliente,
            String nombre,
            String apellido,
            String dni,
            String telefono,
            String tipoEntrega,
            String direccion,
            String distrito,
            String referencia,
            String indicaciones) {

        cliente.setNombre(valor(nombre, cliente.getNombre(), "Cliente"));
        cliente.setApellido(valor(apellido, cliente.getApellido(), "Sin apellido"));
        cliente.setDni(valor(limpiarDni(dni), cliente.getDni(), "00000000"));
        cliente.setTelefono(valor(telefono, cliente.getTelefono(), "Sin telefono"));
        cliente.setDireccion(direccionCliente(tipoEntrega, direccion, cliente.getDireccion()));
        cliente.setDistrito(valor(distrito, cliente.getDistrito(), CIUDAD_DEFAULT));
        cliente.setReferencia(valor(referenciaCompleta(referencia, indicaciones), cliente.getReferencia(), "Sin referencia"));

        return clienteRepository.save(cliente);
    }

    private DireccionEntity guardarDireccion(
            ClienteEntity cliente,
            String tipoEntrega,
            String direccion,
            String distrito,
            String ciudad,
            String referencia,
            String indicaciones) {

        DireccionEntity direccionEntity = direccionRepository.findFirstByCliente_IdOrderByIdAsc(cliente.getId())
                .orElseGet(DireccionEntity::new);

        boolean esRecojo = ENTREGA_RECOJO.equals(normalizarOpcion(tipoEntrega));
        direccionEntity.setCliente(cliente);
        direccionEntity.setAlias(esRecojo ? "Recojo en tienda" : "Delivery");
        direccionEntity.setDireccion(esRecojo ? TIENDA_DIRECCION : valor(direccion, cliente.getDireccion(), "Direccion pendiente"));
        direccionEntity.setReferencia(esRecojo ? "Recojo en tienda" : valor(referenciaCompleta(referencia, indicaciones), null, "Sin referencia"));
        direccionEntity.setCiudad(valor(ciudad, null, CIUDAD_DEFAULT));
        direccionEntity.setDistrito(esRecojo ? CIUDAD_DEFAULT : valor(distrito, cliente.getDistrito(), CIUDAD_DEFAULT));

        return direccionRepository.save(direccionEntity);
    }

    private ComprobanteEntity guardarComprobante(
            ClienteEntity cliente,
            String tipoComprobante,
            String numeroDocumento,
            String razonSocial,
            String direccionFiscal,
            DireccionEntity direccionPedido) {

        String tipo = normalizarOpcion(tipoComprobante);
        if (tipo.isBlank()) {
            tipo = COMPROBANTE_BOLETA;
        }

        boolean esBoleta = COMPROBANTE_BOLETA.equals(tipo);
        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setCliente(cliente);
        comprobante.setTipo(tipo);
        comprobante.setNumeroDocumento(esBoleta
                ? valor(cliente.getDni(), null, "00000000")
                : valor(numeroDocumento, cliente.getDni(), "00000000"));
        comprobante.setRazonSocial(esBoleta ? "Consumidor final" : valor(razonSocial, null, "Sin razon social"));
        comprobante.setDireccionFiscal(esBoleta
                ? valor(direccionPedido.getDireccion(), cliente.getDireccion(), TIENDA_DIRECCION)
                : valor(direccionFiscal, direccionPedido.getDireccion(), TIENDA_DIRECCION));

        return comprobanteRepository.save(comprobante);
    }

    private PedidoEntity guardarPedido(
            ClienteEntity cliente,
            DireccionEntity direccionPedido,
            ComprobanteEntity comprobante,
            List<CartItem> cart) {

        PedidoEntity pedido = new PedidoEntity();
        pedido.setCliente(cliente);
        pedido.setDireccion(direccionPedido);
        pedido.setComprobante(comprobante);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(calcularTotal(cart));
        pedido.setFecha(LocalDateTime.now());
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setUsuario(nombreCompleto(cliente));

        return pedidoRepository.save(pedido);
    }

    private void guardarDetallesPedido(PedidoEntity pedido, List<CartItem> cart) {
        for (CartItem item : cart) {
            Optional<ProductosEntity> producto = buscarProducto(item);
            if (producto.isEmpty()) {
                continue;
            }

            DetallePedidoEntity detalle = new DetallePedidoEntity();
            detalle.setPedido(pedido);
            detalle.setProducto(producto.get());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());

            detallePedidoRepository.save(detalle);
        }
    }

    private void guardarPago(PedidoEntity pedido, String metodoPago) {
        PagoEntity pago = new PagoEntity();
        pago.setPedido(pedido);
        pago.setMetodo(valor(normalizarOpcion(metodoPago), null, "PENDIENTE"));
        pago.setMonto(pedido.getTotal());
        pago.setFechaPago(LocalDateTime.now());

        pagoRepository.save(pago);
    }

    private Optional<ProductosEntity> buscarProducto(CartItem item) {
        try {
            return productoRepository.findById(Long.parseLong(item.getId()));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> obtenerCarrito(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        return cart == null ? List.of() : cart;
    }

    private ClienteEntity obtenerUsuarioLogueado(HttpSession session) {
        return (ClienteEntity) session.getAttribute("usuarioLogueado");
    }

    private double calcularTotal(List<CartItem> cart) {
        return cart.stream().mapToDouble(CartItem::getTotal).sum();
    }

    private String direccionCliente(String tipoEntrega, String direccion, String direccionActual) {
        if (ENTREGA_RECOJO.equals(normalizarOpcion(tipoEntrega))) {
            return TIENDA_DIRECCION;
        }

        return valor(direccion, direccionActual, "Direccion pendiente");
    }

    private String referenciaCompleta(String referencia, String indicaciones) {
        String referenciaLimpia = limpiar(referencia);
        String indicacionesLimpias = limpiar(indicaciones);

        if (referenciaLimpia.isBlank()) {
            return indicacionesLimpias;
        }
        if (indicacionesLimpias.isBlank()) {
            return referenciaLimpia;
        }

        return referenciaLimpia + " - " + indicacionesLimpias;
    }

    private String nombreCompleto(ClienteEntity cliente) {
        return (valor(cliente.getNombre(), null, "Cliente") + " " + valor(cliente.getApellido(), null, "")).trim();
    }

    private String valor(String valor, String respaldo, String defecto) {
        String limpio = limpiar(valor);
        if (!limpio.isBlank()) {
            return limpio;
        }

        String respaldoLimpio = limpiar(respaldo);
        return respaldoLimpio.isBlank() ? defecto : respaldoLimpio;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String limpiarDni(String dni) {
        return limpiar(dni).replaceAll("\\D", "");
    }

    private String normalizarOpcion(String valor) {
        return limpiar(valor).toUpperCase(Locale.ROOT);
    }
}

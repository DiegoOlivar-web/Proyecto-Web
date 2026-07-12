package com.example.Proyecto.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Proyecto.Entity.DetallePedidoEntity;
import com.example.Proyecto.Entity.PedidoEntity;
import com.example.Proyecto.repository.DetallePedidoRepository;
import com.example.Proyecto.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminPedidoService {
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @Transactional(readOnly = true)
    public ResumenPedidos obtenerResumen() {
        List<PedidoEntity> pedidos = pedidoRepository.findAllConClienteDireccion();
        Map<Long, List<DetallePedidoEntity>> detallesPorPedido = pedidos.stream()
                .collect(Collectors.toMap(PedidoEntity::getId,
                        pedido -> detallePedidoRepository.findByPedidoIdConProducto(pedido.getId())));

        long pendientes = pedidos.stream().filter(pedido -> !"ENTREGADO".equals(pedido.getEstado())).count();
        long entregados = pedidos.stream().filter(pedido -> "ENTREGADO".equals(pedido.getEstado())).count();

        return new ResumenPedidos(pedidos, detallesPorPedido, pendientes, entregados);
    }

    public void cambiarEstado(Long id, String estado) {
        if (!"PENDIENTE".equals(estado) && !"ENTREGADO".equals(estado)) {
            return;
        }
        pedidoRepository.findById(id).ifPresent(pedido -> {
            pedido.setEstado(estado);
            pedidoRepository.save(pedido);
        });
    }

    public record ResumenPedidos(
            List<PedidoEntity> pedidos,
            Map<Long, List<DetallePedidoEntity>> detallesPorPedido,
            long pendientes,
            long entregados) {
    }
}

package peru.edu.uls.ucos.operacionesrest.detallepedido;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.pedido.PedidoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoService;

@Service
public class DetallePedidoService {

    private final DetallePedidoRepository detalleRepository;
    private final DetallePedidoMapper detalleMapper;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public DetallePedidoService(DetallePedidoRepository detalleRepository,
                                DetallePedidoMapper detalleMapper,
                                PedidoRepository pedidoRepository,
                                ProductoRepository productoRepository,
                                ProductoService productoService) {
        this.detalleRepository = detalleRepository;
        this.detalleMapper = detalleMapper;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    /**
     * Agrega un detalle de pedido. Dentro de la misma transacción:
     *   1. Valida que el pedido y el producto existan.
     *   2. Reduce el stock del producto en la cantidad solicitada (lanza StockInsuficienteException si no alcanza).
     *   3. Persiste el detalle.
     *   4. Actualiza el total del pedido sumando (cantidad * precioUnitario).
     */
    @Transactional
    public DetallePedidoResponse agregarDetalle(DetallePedidoRequest request) {
        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del detalle debe ser mayor que cero.");
        }
        if (request.precioUnitario() == null || request.precioUnitario() < 0) {
            throw new IllegalArgumentException("El precio unitario del detalle debe ser mayor o igual que cero.");
        }

        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Pedido no encontrado con ID: " + request.pedidoId()));

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto no encontrado con ID: " + request.productoId()));

        // 1. Reducir stock del producto (valida internamente que el stock alcance).
        productoService.reducirStock(producto.getId(), request.cantidad());

        // 2. Crear y guardar el detalle.
        DetallePedido detalle = detalleMapper.aEntidad(pedido, producto, request);
        DetallePedido detalleGuardado = detalleRepository.save(detalle);

        // 3. Actualizar el total del pedido sumando el subtotal del detalle.
        Double subtotalDetalle = detalleGuardado.getCantidad() * detalleGuardado.getPrecioUnitario();
        Double totalActual = pedido.getTotal() == null ? 0.0 : pedido.getTotal();
        pedido.setTotal(totalActual + subtotalDetalle);
        pedidoRepository.save(pedido);

        return detalleMapper.aRespuesta(detalleGuardado);
    }

    @Transactional(readOnly = true)
    public DetallePedidoResponse consultarPorId(Long id) {
        return detalleRepository.findById(id)
                .map(detalleMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Detalle de pedido no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<DetallePedidoResponse> listarPorPedido(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new RecursoNoEncontradoException("Pedido no encontrado con ID: " + pedidoId);
        }
        return detalleRepository.findByPedidoId(pedidoId)
                .stream()
                .map(detalleMapper::aRespuesta)
                .toList();
    }
}

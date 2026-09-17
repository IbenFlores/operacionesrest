package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Service;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoService(PedidoRepository pedidoRepository, PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoMapper = pedidoMapper;
    }

    // 1. Consulta por ID
    public PedidoResponse consultarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(pedidoMapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado con ID: " + id));
    }

    // 2. Registro nuevo (Validación de duplicados por 'numeroPedido')
    public PedidoResponse registrarProductoNuevo(PedidoRequest request) {
        if (pedidoRepository.existsByNumeroPedido(request.numeroPedido())) {
            throw new RecursoDuplicadoException("El pedido con número " + request.numeroPedido() + " ya existe.");
        }
        Pedido nuevoPedido = pedidoMapper.aEntidad(request);
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        return pedidoMapper.aRespuesta(pedidoGuardado);
    }

    // 3. Consulta por campo distinto al ID (por estado)
    public List<PedidoResponse> consultarPorEstado(String estado) {
        return pedidoRepository.findByEstadoIgnoreCase(estado)
                .stream()
                .map(pedidoMapper::aRespuesta)
                .toList();
    }
}
package peru.edu.uls.ucos.operacionesrest.pedido;

import org.springframework.stereotype.Component;

@Component 
public class PedidoMapper {

    public Pedido aEntidad(PedidoRequest request) {
        return new Pedido(
            request.numeroPedido(),
            request.total(),
            request.estado()
        );

    }

    public PedidoResponse aRespuesta(Pedido pedido) {
        return new PedidoResponse(
            pedido.getId(),
            pedido.getNumeroPedido(),
            pedido.getTotal(),
            pedido.getEstado()
        );
    }
}
package peru.edu.uls.ucos.operacionesrest.detallepedido;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import peru.edu.uls.ucos.operacionesrest.pedido.Pedido;
import peru.edu.uls.ucos.operacionesrest.pedido.PedidoRepository; // Ajusta el paquete si cambia
import peru.edu.uls.ucos.operacionesrest.producto.Producto;
import peru.edu.uls.ucos.operacionesrest.producto.ProductoRepository; // Ajusta el paquete si cambia

@RestController
@RequestMapping("/api/detalles")
public class DetallePedidoController {

    @Autowired
    private DetallePedidoRepository detalleRepo;

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @PostMapping
    public DetallePedido guardarDetalle(@RequestParam Long pedidoId, 
                                        @RequestParam Long productoId, 
                                        @RequestParam Integer cantidad, 
                                        @RequestParam Double precio) {
        
        Pedido pedido = pedidoRepo.findById(pedidoId).orElseThrow();
        Producto producto = productoRepo.findById(productoId).orElseThrow();

        DetallePedido detalle = new DetallePedido(pedido, producto, cantidad, precio);
        return detalleRepo.save(detalle);
    }
}
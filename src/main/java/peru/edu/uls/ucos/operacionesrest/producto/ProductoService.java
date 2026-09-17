package peru.edu.uls.ucos.operacionesrest.producto;

import java.util.List;

import org.springframework.stereotype.Service;

import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoDuplicadoException;
import peru.edu.uls.ucos.operacionesrest.excepciones.RecursoNoEncontradoException;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    public ProductoService(ProductoRepository repository, ProductoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ProductoResponse registrarProductoNuevo(ProductoRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new RecursoDuplicadoException("Ya existe un producto registrado con el nombre: " + request.nombre());
        }
        Producto nuevoProducto = mapper.aEntidad(request);
        Producto productoGuardado = repository.save(nuevoProducto);
        return mapper.aRespuesta(productoGuardado);
    }

    public ProductoResponse consultarProductoPorId(Long id) {
        return repository.findById(id)
                .map(mapper::aRespuesta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con el ID: " + id));
    }

    public List<ProductoResponse> consultarProductoPorMarca(String marca) {
        List<Producto> productos = repository.findByMarcaIgnoreCase(marca);
        if (productos.isEmpty()) {
            throw new RecursoNoEncontradoException("No se encontraron productos de la marca: " + marca);
        }
        return productos.stream().map(mapper::aRespuesta).toList();
    }
}
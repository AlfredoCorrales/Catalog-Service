package com.distrilink.catalog.service;

import com.distrilink.catalog.dto.ProductoRequest;
import com.distrilink.catalog.dto.ProductoResponse;
import com.distrilink.catalog.exception.ProductoNotFoundException;
import com.distrilink.catalog.model.EstadoProducto;
import com.distrilink.catalog.model.Producto;
import com.distrilink.catalog.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Capa de Services: aquí viven las reglas de negocio, no en el Controller. */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repository;

    @Transactional
    public ProductoResponse create(ProductoRequest request) {
        Producto producto = new Producto();
        apply(request, producto);
        return ProductoResponse.from(repository.save(producto));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> findAll(Long distribuidorId) {
        List<Producto> result = (distribuidorId == null)
                ? repository.findAll()
                : repository.findByDistribuidorIdOrderByNombreAsc(distribuidorId);
        return result.stream().map(ProductoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse findById(Long id) {
        return ProductoResponse.from(getOrThrow(id));
    }

    @Transactional
    public ProductoResponse update(Long id, ProductoRequest request) {
        Producto producto = getOrThrow(id);
        apply(request, producto);
        return ProductoResponse.from(repository.save(producto));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Producto getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductoNotFoundException(id));
    }

    /** Regla de negocio: el nombre se guarda sin espacios sobrantes y el estado por defecto es ACTIVO. */
    private void apply(ProductoRequest request, Producto producto) {
        producto.setDistribuidorId(request.distribuidorId());
        producto.setNombre(request.nombre().trim());
        producto.setPrecio(request.precio());
        producto.setExistencias(request.existencias());
        producto.setEstado(request.estado() != null ? request.estado() : EstadoProducto.ACTIVO);
    }
}

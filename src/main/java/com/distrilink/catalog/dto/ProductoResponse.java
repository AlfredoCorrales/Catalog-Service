package com.distrilink.catalog.dto;

import com.distrilink.catalog.model.EstadoProducto;
import com.distrilink.catalog.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/** DTO de salida: lo que devuelve la API. Nunca se devuelve la @Entity directamente. */
@Schema(description = "Producto devuelto por la API")
public record ProductoResponse(
        Long id,
        Long distribuidorId,
        String nombre,
        BigDecimal precio,
        Integer existencias,
        EstadoProducto estado
) {

    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getDistribuidorId(), p.getNombre(),
                p.getPrecio(), p.getExistencias(), p.getEstado());
    }
}

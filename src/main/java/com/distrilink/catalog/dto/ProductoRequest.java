package com.distrilink.catalog.dto;

import com.distrilink.catalog.model.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** DTO de entrada: lo que el cliente envía para crear o actualizar un producto. */
@Schema(description = "Datos para crear o actualizar un producto")
public record ProductoRequest(

        @Schema(description = "Id del distribuidor dueño del producto", example = "3")
        @NotNull(message = "distribuidorId es obligatorio")
        @Positive(message = "distribuidorId debe ser positivo")
        Long distribuidorId,

        @Schema(description = "Nombre del producto", example = "Arroz Diana 500g")
        @NotBlank(message = "nombre es obligatorio")
        @Size(max = 120, message = "nombre admite máximo 120 caracteres")
        String nombre,

        @Schema(description = "Precio unitario en pesos", example = "3200.00")
        @NotNull(message = "precio es obligatorio")
        @DecimalMin(value = "0.01", message = "precio debe ser mayor que 0")
        @Digits(integer = 10, fraction = 2, message = "precio admite hasta 10 enteros y 2 decimales")
        BigDecimal precio,

        @Schema(description = "Unidades disponibles en inventario", example = "150")
        @NotNull(message = "existencias es obligatorio")
        @Min(value = 0, message = "existencias no puede ser negativa")
        Integer existencias,

        @Schema(description = "Estado del producto. Si no se envía, se usa ACTIVO", example = "ACTIVO")
        EstadoProducto estado
) {
}

package com.distrilink.catalog.controller;

import com.distrilink.catalog.dto.ProductoRequest;
import com.distrilink.catalog.dto.ProductoResponse;
import com.distrilink.catalog.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Capa de Controllers: recibe HTTP, valida el DTO y delega en el Service. */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "CRUD del catálogo de productos de DistriLink")
public class ProductoController {

    private final ProductoService service;

    @PostMapping
    @Operation(summary = "Crear un producto")
    @ApiResponse(responseCode = "201", description = "Producto creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    public ResponseEntity<ProductoResponse> create(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/productos/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Si se envía distribuidorId, devuelve solo los de ese distribuidor, ordenados por nombre")
    public List<ProductoResponse> findAll(
            @Parameter(description = "Id del distribuidor", example = "3")
            @RequestParam(required = false) Long distribuidorId) {
        return service.findAll(distribuidorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un producto por id")
    @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    public ProductoResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto")
    @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    public ProductoResponse update(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un producto")
    @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

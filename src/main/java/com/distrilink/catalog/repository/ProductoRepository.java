package com.distrilink.catalog.repository;

import com.distrilink.catalog.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Capa de Repositories: Spring Data genera el SQL a partir del nombre del método. */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByDistribuidorIdOrderByNombreAsc(Long distribuidorId);
}

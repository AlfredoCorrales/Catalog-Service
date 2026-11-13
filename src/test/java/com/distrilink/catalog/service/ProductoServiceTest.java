package com.distrilink.catalog.service;

import com.distrilink.catalog.dto.ProductoRequest;
import com.distrilink.catalog.dto.ProductoResponse;
import com.distrilink.catalog.exception.ProductoNotFoundException;
import com.distrilink.catalog.model.EstadoProducto;
import com.distrilink.catalog.model.Producto;
import com.distrilink.catalog.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Prueba unitaria del Service: el repositorio es un mock, no se usa base de datos. */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private ProductoRequest request(EstadoProducto estado) {
        return new ProductoRequest(3L, "  Arroz Diana 500g  ", new BigDecimal("3200.00"), 150, estado);
    }

    private Producto saved(Long id) {
        Producto p = new Producto();
        p.setId(id);
        p.setDistribuidorId(3L);
        p.setNombre("Arroz Diana 500g");
        p.setPrecio(new BigDecimal("3200.00"));
        p.setExistencias(150);
        p.setEstado(EstadoProducto.ACTIVO);
        return p;
    }

    @Test
    void create_guardaConEstadoPorDefectoYNombreLimpio() {
        when(repository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductoResponse response = service.create(request(null));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.estado()).isEqualTo(EstadoProducto.ACTIVO);
        assertThat(response.nombre()).isEqualTo("Arroz Diana 500g");
        assertThat(response.existencias()).isEqualTo(150);
    }

    @Test
    void findAll_conDistribuidorIdFiltraPorDistribuidor() {
        when(repository.findByDistribuidorIdOrderByNombreAsc(3L)).thenReturn(List.of(saved(1L)));

        List<ProductoResponse> result = service.findAll(3L);

        assertThat(result).hasSize(1);
        verify(repository, never()).findAll();
    }

    @Test
    void findAll_sinFiltroDevuelveTodos() {
        when(repository.findAll()).thenReturn(List.of(saved(1L), saved(2L)));

        assertThat(service.findAll(null)).hasSize(2);
    }

    @Test
    void findById_inexistenteLanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ProductoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_cambiaLosCamposDelProducto() {
        when(repository.findById(1L)).thenReturn(Optional.of(saved(1L)));
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoRequest cambio = new ProductoRequest(3L, "Arroz Diana 1kg", new BigDecimal("6100.00"), 0,
                EstadoProducto.INACTIVO);
        ProductoResponse response = service.update(1L, cambio);

        assertThat(response.nombre()).isEqualTo("Arroz Diana 1kg");
        assertThat(response.precio()).isEqualByComparingTo("6100.00");
        assertThat(response.existencias()).isZero();
        assertThat(response.estado()).isEqualTo(EstadoProducto.INACTIVO);
    }

    @Test
    void delete_inexistenteNoBorraNada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(ProductoNotFoundException.class);
        verify(repository, never()).delete(any());
    }
}

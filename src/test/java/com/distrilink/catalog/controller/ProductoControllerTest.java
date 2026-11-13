package com.distrilink.catalog.controller;

import com.distrilink.catalog.dto.ProductoRequest;
import com.distrilink.catalog.dto.ProductoResponse;
import com.distrilink.catalog.exception.ProductoNotFoundException;
import com.distrilink.catalog.model.EstadoProducto;
import com.distrilink.catalog.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba del Controller: solo levanta la capa web; el Service es un mock. */
@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductoService service;

    private static final String VALID_JSON = """
            {
              "distribuidorId": 3,
              "nombre": "Arroz Diana 500g",
              "precio": 3200.00,
              "existencias": 150
            }
            """;

    private ProductoResponse response() {
        return new ProductoResponse(1L, 3L, "Arroz Diana 500g", new BigDecimal("3200.00"), 150,
                EstadoProducto.ACTIVO);
    }

    @Test
    void post_valido_devuelve201ConElDto() throws Exception {
        when(service.create(any(ProductoRequest.class))).thenReturn(response());

        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/productos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.existencias").value(150));
    }

    @Test
    void post_sinNombre_devuelve400ConElCampo() throws Exception {
        String invalid = """
                { "distribuidorId": 3, "nombre": "", "precio": 3200.00, "existencias": 10 }
                """;

        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre").exists());
    }

    @Test
    void post_precioYExistenciasInvalidos_devuelve400() throws Exception {
        String invalid = """
                { "distribuidorId": 3, "nombre": "Arroz", "precio": 0, "existencias": -5 }
                """;

        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.precio").exists())
                .andExpect(jsonPath("$.errors.existencias").exists());
    }

    @Test
    void get_inexistente_devuelve404() throws Exception {
        when(service.findById(99L)).thenThrow(new ProductoNotFoundException(99L));

        mvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Producto no encontrado"));
    }

    @Test
    void delete_devuelve204() throws Exception {
        mvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}

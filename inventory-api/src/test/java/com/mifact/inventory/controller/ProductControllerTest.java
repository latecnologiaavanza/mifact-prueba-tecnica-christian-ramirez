package com.mifact.inventory.controller;

import com.mifact.inventory.config.RateLimitInterceptor;
import com.mifact.inventory.dto.ProductResponse;
import com.mifact.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private RateLimitInterceptor rateLimitInterceptor;

    @BeforeEach
    void permitirSolicitudesEnLaPrueba() {
        when(rateLimitInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void debeDevolverLaListaDeProductos() throws Exception {
        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setName("Laptop Lenovo");
        product.setQuantity(10);
        product.setPrice(new BigDecimal("2499.90"));
        when(productService.findAll(isNull())).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop Lenovo"));
    }

    @Test
    void debeRechazarUnProductoInvalido() throws Exception {
        String body = "{\"name\":\"\",\"quantity\":-1,\"price\":0}";

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.quantity").exists())
                .andExpect(jsonPath("$.fieldErrors.price").exists());
    }
}

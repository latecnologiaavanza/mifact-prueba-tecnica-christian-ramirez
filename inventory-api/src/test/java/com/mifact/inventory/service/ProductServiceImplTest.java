package com.mifact.inventory.service;

import com.mifact.inventory.dto.ProductRequest;
import com.mifact.inventory.dto.ProductResponse;
import com.mifact.inventory.entity.Product;
import com.mifact.inventory.exception.ResourceNotFoundException;
import com.mifact.inventory.repository.ProductRepository;
import com.mifact.inventory.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void debeListarProductosOrdenados() {
        Product product = product(1L, "Laptop Lenovo", 10, "2499.90");
        when(productRepository.findAllByOrderByNameAsc()).thenReturn(Collections.singletonList(product));

        assertEquals("Laptop Lenovo", productService.findAll(null).get(0).getName());
        verify(productRepository).findAllByOrderByNameAsc();
    }

    @Test
    void debeBuscarPorNombre() {
        Product product = product(1L, "Laptop Lenovo", 10, "2499.90");
        when(productRepository.findByNameContainingIgnoreCaseOrderByNameAsc("lenovo"))
                .thenReturn(Arrays.asList(product));

        assertEquals(1, productService.findAll(" lenovo ").size());
        verify(productRepository).findByNameContainingIgnoreCaseOrderByNameAsc("lenovo");
    }

    @Test
    void debeCrearProducto() {
        ProductRequest request = request(" Teclado ", "Accesorio", 5, "99.90");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ProductResponse response = productService.create(request);

        assertEquals(2L, response.getId());
        assertEquals("Teclado", response.getName());
        assertEquals(new BigDecimal("99.90"), response.getPrice());
    }

    @Test
    void debeLanzarExcepcionSiNoExiste() {
        when(productRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(99L));
    }

    private Product product(Long id, String name, int quantity, String price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setQuantity(quantity);
        product.setPrice(new BigDecimal(price));
        return product;
    }

    private ProductRequest request(String name, String description, int quantity, String price) {
        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setQuantity(quantity);
        request.setPrice(new BigDecimal(price));
        return request;
    }
}

package com.mifact.inventory.repository;

import com.mifact.inventory.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void debeBuscarProductosPorNombreSinDistinguirMayusculas() {
        Product product = new Product();
        product.setName("Laptop Lenovo");
        product.setQuantity(10);
        product.setPrice(new BigDecimal("2499.90"));
        productRepository.save(product);

        java.util.List<Product> result = productRepository
                .findByNameContainingIgnoreCaseOrderByNameAsc("lenovo");

        assertFalse(result.isEmpty());
        assertEquals("Laptop Lenovo", result.get(0).getName());
    }
}

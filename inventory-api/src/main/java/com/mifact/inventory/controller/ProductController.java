package com.mifact.inventory.controller;

import com.mifact.inventory.dto.ProductRequest;
import com.mifact.inventory.dto.ProductResponse;
import com.mifact.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Validated
@Tag(name = "Productos", description = "Operaciones de mantenimiento de productos")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Lista todos los productos o filtra por nombre")
    public ResponseEntity<List<ProductResponse>> findAll(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(productService.findAll(name));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por id")
    public ResponseEntity<ProductResponse> findById(@PathVariable @Positive(message = "El id debe ser positivo") Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductResponse> update(
            @PathVariable @Positive(message = "El id debe ser positivo") Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "El id debe ser positivo") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

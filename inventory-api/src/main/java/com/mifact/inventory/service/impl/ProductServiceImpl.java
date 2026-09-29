package com.mifact.inventory.service.impl;

import com.mifact.inventory.dto.ProductRequest;
import com.mifact.inventory.dto.ProductResponse;
import com.mifact.inventory.entity.Product;
import com.mifact.inventory.exception.ResourceNotFoundException;
import com.mifact.inventory.repository.ProductRepository;
import com.mifact.inventory.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll(String name) {
        List<Product> products = name == null || name.trim().isEmpty()
                ? productRepository.findAllByOrderByNameAsc()
                : productRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name.trim());
        return products.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return toResponse(findProduct(id));
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        copyRequest(request, product);
        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        copyRequest(request, product);
        return toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con id: " + id));
    }

    private void copyRequest(ProductRequest request, Product product) {
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setQuantity(product.getQuantity());
        response.setPrice(product.getPrice());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}

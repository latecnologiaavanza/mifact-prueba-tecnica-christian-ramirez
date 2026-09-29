package com.mifact.inventory.service;

import com.mifact.inventory.dto.ProductRequest;
import com.mifact.inventory.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> findAll(String name);
    ProductResponse findById(Long id);
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
}

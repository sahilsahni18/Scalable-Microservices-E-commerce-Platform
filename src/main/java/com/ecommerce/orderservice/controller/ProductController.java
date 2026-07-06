package com.ecommerce.orderservice.controller;

import com.ecommerce.orderservice.entity.Product;
import com.ecommerce.orderservice.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // ✅ ADD THIS CONSTRUCTOR (IMPORTANT)
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PatchMapping("/{id}")
    public Product update(@PathVariable UUID id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }
}
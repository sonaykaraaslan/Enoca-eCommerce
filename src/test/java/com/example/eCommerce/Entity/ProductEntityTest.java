package com.example.eCommerce.Entity;

import com.example.eCommerce.Repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductEntityTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testProductCreation() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Test Product", savedProduct.getName());
        assertEquals(100.0, savedProduct.getPrice());
        assertEquals(10, savedProduct.getStock());
    }

    @Test
    void testProductUpdate() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);

        product.setName("Updated Product");
        product.setPrice(150.0);
        product.setStock(20);

        Product updatedProduct = productRepository.save(product);

        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(150.0, updatedProduct.getPrice());
        assertEquals(20, updatedProduct.getStock());
    }

    @Test
    void testProductStockUpdate() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);

        product.setStock(5);
        Product updatedProduct = productRepository.save(product);

        assertEquals(5, updatedProduct.getStock());
    }

    @Test
    void testProductPriceUpdate() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);

        product.setPrice(120.0);
        Product updatedProduct = productRepository.save(product);

        assertEquals(120.0, updatedProduct.getPrice());
    }
} 
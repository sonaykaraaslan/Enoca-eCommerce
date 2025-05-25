package com.example.eCommerce.Service;

import com.example.eCommerce.Entity.Product;
import com.example.eCommerce.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);
    }

    @Test
    void testCreateProduct() {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setPrice(150.0);
        newProduct.setStock(20);

        Product savedProduct = productService.createProduct(newProduct);

        assertNotNull(savedProduct.getId());
        assertEquals("New Product", savedProduct.getName());
        assertEquals(150.0, savedProduct.getPrice());
        assertEquals(20, savedProduct.getStock());
    }

    @Test
    void testGetProductById() {
        Optional<Product> foundProduct = productService.getProductById(product.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals(product.getId(), foundProduct.get().getId());
        assertEquals("Test Product", foundProduct.get().getName());
    }

    @Test
    void testGetAllProducts() {
        // İkinci ürün ekle
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(200.0);
        product2.setStock(15);
        productRepository.save(product2);

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Test Product")));
        assertTrue(products.stream().anyMatch(p -> p.getName().equals("Test Product 2")));
    }

    @Test
    void testUpdateProduct() {
        product.setName("Updated Product");
        product.setPrice(120.0);
        product.setStock(25);

        Product updatedProduct = productService.updateProduct(product.getId(), product);

        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(120.0, updatedProduct.getPrice());
        assertEquals(25, updatedProduct.getStock());
    }

    @Test
    void testUpdateProductStock() {
        Product updatedProduct = productService.updateProductStock(product.getId(), 5);

        assertEquals(5, updatedProduct.getStock());
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(product.getId());

        Optional<Product> deletedProduct = productRepository.findById(product.getId());
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void testProductStockValidation() {
        // Stok miktarı negatif olamaz
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductStock(product.getId(), -1);
        });

        // Stok miktarı mevcut stoktan fazla olamaz
        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductStock(product.getId(), product.getStock() + 1);
        });
    }
} 
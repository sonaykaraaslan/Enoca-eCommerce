package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Product;
import com.example.eCommerce.Repository.ProductRepository;
import com.example.eCommerce.Service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private Product product;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/products";

        // Test verilerini hazırla
        product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);
    }

    @Test
    void testCreateProductWithRealDatabase() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setPrice(150.0);
        newProduct.setStock(20);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(newProduct),
                headers
        );

        ResponseEntity<Product> response = restTemplate.postForEntity(
                baseUrl,
                request,
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Product", response.getBody().getName());
        assertEquals(150.0, response.getBody().getPrice());
        assertEquals(20, response.getBody().getStock());
    }

    @Test
    void testGetProductById() {
        ResponseEntity<Product> response = restTemplate.getForEntity(
                baseUrl + "/" + product.getId(),
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(product.getId(), response.getBody().getId());
        assertEquals("Test Product", response.getBody().getName());
        assertEquals(100.0, response.getBody().getPrice());
    }

    @Test
    void testGetAllProducts() {
        // İkinci ürün ekle
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(200.0);
        product2.setStock(15);
        productRepository.save(product2);

        ResponseEntity<List<Product>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Product>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(p -> p.getName().equals("Test Product")));
        assertTrue(response.getBody().stream().anyMatch(p -> p.getName().equals("Test Product 2")));
    }

    @Test
    void testUpdateProduct() throws Exception {
        product.setName("Updated Product");
        product.setPrice(120.0);
        product.setStock(25);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(product),
                headers
        );

        ResponseEntity<Product> response = restTemplate.exchange(
                baseUrl + "/" + product.getId(),
                HttpMethod.PUT,
                request,
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Product", response.getBody().getName());
        assertEquals(120.0, response.getBody().getPrice());
        assertEquals(25, response.getBody().getStock());
    }

    @Test
    void testUpdateProductStock() {
        // Ürün stok güncelleme
        ResponseEntity<Product> response = restTemplate.exchange(
                baseUrl + "/" + product.getId() + "/stock?quantity=5",
                HttpMethod.PUT,
                null,
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getStock());
    }

    @Test
    void testProductInCart() {
        // Ürünün sepete eklenip eklenemeyeceğini test et
        ResponseEntity<Product> response = restTemplate.getForEntity(
                baseUrl + "/" + product.getId(),
                Product.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getStock() > 0);
    }
} 
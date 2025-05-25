package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Cart;
import com.example.eCommerce.Entity.CartItem;
import com.example.eCommerce.Entity.Customer;
import com.example.eCommerce.Entity.Product;
import com.example.eCommerce.Repository.CartRepository;
import com.example.eCommerce.Repository.CustomerRepository;
import com.example.eCommerce.Repository.ProductRepository;
import com.example.eCommerce.Service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class CartControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;
    private Customer customer;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/carts";

        // Test verilerini hazırla
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer = customerRepository.save(customer);

        product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);

        cart = new Cart();
        cart.setCustomer(customer);
        cart.setTotalAmount(0.0);
        cart = cartRepository.save(cart);
    }

    @Test
    void testCreateCartWithRealDatabase() {
        // Yeni bir müşteri oluştur
        Customer newCustomer = new Customer();
        newCustomer.setName("New Customer");
        newCustomer.setEmail("new@example.com");
        newCustomer = customerRepository.save(newCustomer);

        // Cart oluştur
        ResponseEntity<Cart> response = restTemplate.postForEntity(
                baseUrl + "?customerId=" + newCustomer.getId(),
                null,
                Cart.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newCustomer.getId(), response.getBody().getCustomer().getId());
        assertEquals(0.0, response.getBody().getTotalAmount());
    }

    @Test
    void testAddItemToCartWithRealDatabase() {
        // Sepete ürün ekle
        ResponseEntity<Cart> response = restTemplate.postForEntity(
                baseUrl + "/" + cart.getId() + "/items?productId=" + product.getId() + "&quantity=2",
                null,
                Cart.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200.0, response.getBody().getTotalAmount());
        assertEquals(1, response.getBody().getCartItems().size());
    }

    @Test
    void testGetCartWithItems() {
        // Önce sepete ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // Sepeti getir
        ResponseEntity<Cart> response = restTemplate.getForEntity(
                baseUrl + "/" + cart.getId(),
                Cart.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(200.0, response.getBody().getTotalAmount());
        assertEquals(1, response.getBody().getCartItems().size());
        assertEquals(product.getId(), response.getBody().getCartItems().get(0).getProduct().getId());
    }

    @Test
    void testRemoveItemFromCartWithRealDatabase() {
        // Önce sepete ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // Ürünü sepetten çıkar
        restTemplate.delete(baseUrl + "/" + cart.getId() + "/items/" + product.getId());

        // Sepeti kontrol et
        ResponseEntity<Cart> response = restTemplate.getForEntity(
                baseUrl + "/" + cart.getId(),
                Cart.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0.0, response.getBody().getTotalAmount());
        assertTrue(response.getBody().getCartItems().isEmpty());
    }

    @Test
    void testCartTotalAmountCalculation() {
        // İki farklı ürün ekle
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2 = productRepository.save(product2);

        cartService.addItemToCart(cart.getId(), product.getId(), 2);  // 200 TL
        cartService.addItemToCart(cart.getId(), product2.getId(), 3); // 150 TL

        ResponseEntity<Cart> response = restTemplate.getForEntity(
                baseUrl + "/" + cart.getId(),
                Cart.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(350.0, response.getBody().getTotalAmount());
        assertEquals(2, response.getBody().getCartItems().size());
    }
} 
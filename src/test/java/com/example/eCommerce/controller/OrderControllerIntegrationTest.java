package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.*;
import com.example.eCommerce.Repository.*;
import com.example.eCommerce.Service.OrderService;
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
class OrderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    private String baseUrl;
    private Customer customer;
    private Cart cart;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/orders";

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

        order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(150.0);
        order.setStatus("PENDING");
        order = orderRepository.save(order);
    }

    @Test
    void testCreateOrderWithRealDatabase() {
        // Sepete ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // Sipariş oluştur
        ResponseEntity<Order> response = restTemplate.postForEntity(
                baseUrl + "?customerId=" + customer.getId(),
                null,
                Order.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(customer.getId(), response.getBody().getCustomer().getId());
        assertEquals(200.0, response.getBody().getTotalAmount());
        assertEquals("PENDING", response.getBody().getStatus());
    }

    @Test
    void testGetOrderById() {
        ResponseEntity<Order> response = restTemplate.getForEntity(
                baseUrl + "/" + order.getId(),
                Order.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(order.getId(), response.getBody().getId());
        assertEquals(customer.getId(), response.getBody().getCustomer().getId());
        assertEquals(150.0, response.getBody().getTotalAmount());
    }

    @Test
    void testUpdateOrderStatus() {
        // Sipariş durumunu güncelle
        ResponseEntity<Order> response = restTemplate.exchange(
                baseUrl + "/" + order.getId() + "/status?status=COMPLETED",
                HttpMethod.PUT,
                null,
                Order.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("COMPLETED", response.getBody().getStatus());
    }

    @Test
    void testGetCustomerOrders() {
        // Müşterinin siparişlerini getir
        ResponseEntity<List<Order>> response = restTemplate.exchange(
                baseUrl + "/customer/" + customer.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Order>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(order.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testOrderWithMultipleItems() {
        // İkinci ürün ekle
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2 = productRepository.save(product2);

        // Sepete iki farklı ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);  // 200 TL
        cartService.addItemToCart(cart.getId(), product2.getId(), 3); // 150 TL

        // Sipariş oluştur
        ResponseEntity<Order> response = restTemplate.postForEntity(
                baseUrl + "?customerId=" + customer.getId(),
                null,
                Order.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(350.0, response.getBody().getTotalAmount());
        assertEquals(2, response.getBody().getOrderItems().size());
    }
} 
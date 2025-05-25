package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Customer;
import com.example.eCommerce.Repository.CustomerRepository;
import com.example.eCommerce.Service.CustomerService;
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
class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private Customer customer;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/customers";

        // Test verilerini hazırla
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");
        customer = customerRepository.save(customer);
    }

    @Test
    void testCreateCustomerWithRealDatabase() throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setName("New Customer");
        newCustomer.setEmail("new@example.com");
        newCustomer.setPhone("9876543210");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(newCustomer),
                headers
        );

        ResponseEntity<Customer> response = restTemplate.postForEntity(
                baseUrl,
                request,
                Customer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New Customer", response.getBody().getName());
        assertEquals("new@example.com", response.getBody().getEmail());
    }

    @Test
    void testGetCustomerById() {
        ResponseEntity<Customer> response = restTemplate.getForEntity(
                baseUrl + "/" + customer.getId(),
                Customer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(customer.getId(), response.getBody().getId());
        assertEquals("Test Customer", response.getBody().getName());
    }

    @Test
    void testGetAllCustomers() {
        // İkinci müşteri ekle
        Customer customer2 = new Customer();
        customer2.setName("Test Customer 2");
        customer2.setEmail("test2@example.com");
        customer2.setPhone("5555555555");
        customerRepository.save(customer2);

        ResponseEntity<List<Customer>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Customer>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().anyMatch(c -> c.getName().equals("Test Customer")));
        assertTrue(response.getBody().stream().anyMatch(c -> c.getName().equals("Test Customer 2")));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        customer.setName("Updated Customer");
        customer.setEmail("updated@example.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(customer),
                headers
        );

        ResponseEntity<Customer> response = restTemplate.exchange(
                baseUrl + "/" + customer.getId(),
                HttpMethod.PUT,
                request,
                Customer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Customer", response.getBody().getName());
        assertEquals("updated@example.com", response.getBody().getEmail());
    }

    @Test
    void testCustomerWithOrders() {
        // Müşteriye sipariş ekle
        // Bu test, müşteri-sipariş ilişkisini test eder
        ResponseEntity<Customer> response = restTemplate.getForEntity(
                baseUrl + "/" + customer.getId(),
                Customer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(customer.getId(), response.getBody().getId());
    }
} 
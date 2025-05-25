package com.example.eCommerce.Service;

import com.example.eCommerce.Entity.Customer;
import com.example.eCommerce.Repository.CustomerRepository;
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
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");
        customer = customerRepository.save(customer);
    }

    @Test
    void testCreateCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setName("New Customer");
        newCustomer.setEmail("new@example.com");
        newCustomer.setPhone("9876543210");

        Customer savedCustomer = customerService.createCustomer(newCustomer);

        assertNotNull(savedCustomer.getId());
        assertEquals("New Customer", savedCustomer.getName());
        assertEquals("new@example.com", savedCustomer.getEmail());
        assertEquals("9876543210", savedCustomer.getPhone());
    }

    @Test
    void testGetCustomerById() {
        Optional<Customer> foundCustomer = customerService.getCustomerById(customer.getId());

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getId(), foundCustomer.get().getId());
        assertEquals("Test Customer", foundCustomer.get().getName());
    }

    @Test
    void testGetAllCustomers() {
        // İkinci müşteri ekle
        Customer customer2 = new Customer();
        customer2.setName("Test Customer 2");
        customer2.setEmail("test2@example.com");
        customer2.setPhone("5555555555");
        customerRepository.save(customer2);

        List<Customer> customers = customerService.getAllCustomers();

        assertEquals(2, customers.size());
        assertTrue(customers.stream().anyMatch(c -> c.getName().equals("Test Customer")));
        assertTrue(customers.stream().anyMatch(c -> c.getName().equals("Test Customer 2")));
    }

    @Test
    void testUpdateCustomer() {
        customer.setName("Updated Customer");
        customer.setEmail("updated@example.com");
        customer.setPhone("9876543210");

        Customer updatedCustomer = customerService.updateCustomer(customer.getId(), customer);

        assertEquals("Updated Customer", updatedCustomer.getName());
        assertEquals("updated@example.com", updatedCustomer.getEmail());
        assertEquals("9876543210", updatedCustomer.getPhone());
    }

    @Test
    void testDeleteCustomer() {
        customerService.deleteCustomer(customer.getId());

        Optional<Customer> deletedCustomer = customerRepository.findById(customer.getId());
        assertFalse(deletedCustomer.isPresent());
    }

    @Test
    void testCustomerEmailValidation() {
        // Aynı email ile müşteri oluşturulamaz
        Customer duplicateCustomer = new Customer();
        duplicateCustomer.setName("Duplicate Customer");
        duplicateCustomer.setEmail("test@example.com");
        duplicateCustomer.setPhone("5555555555");

        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(duplicateCustomer);
        });
    }

    @Test
    void testCustomerPhoneValidation() {
        // Geçersiz telefon numarası
        Customer invalidCustomer = new Customer();
        invalidCustomer.setName("Invalid Customer");
        invalidCustomer.setEmail("invalid@example.com");
        invalidCustomer.setPhone("123"); // Çok kısa

        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(invalidCustomer);
        });
    }
} 
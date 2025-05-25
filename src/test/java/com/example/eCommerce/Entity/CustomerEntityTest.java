package com.example.eCommerce.Entity;

import com.example.eCommerce.Repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerEntityTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testCustomerCreation() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");

        Customer savedCustomer = customerRepository.save(customer);

        assertNotNull(savedCustomer.getId());
        assertEquals("Test Customer", savedCustomer.getName());
        assertEquals("test@example.com", savedCustomer.getEmail());
        assertEquals("1234567890", savedCustomer.getPhone());
    }

    @Test
    void testCustomerUpdate() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");
        customer = customerRepository.save(customer);

        customer.setName("Updated Customer");
        customer.setEmail("updated@example.com");
        customer.setPhone("9876543210");

        Customer updatedCustomer = customerRepository.save(customer);

        assertEquals("Updated Customer", updatedCustomer.getName());
        assertEquals("updated@example.com", updatedCustomer.getEmail());
        assertEquals("9876543210", updatedCustomer.getPhone());
    }

    @Test
    void testCustomerEmailUpdate() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");
        customer = customerRepository.save(customer);

        customer.setEmail("new@example.com");
        Customer updatedCustomer = customerRepository.save(customer);

        assertEquals("new@example.com", updatedCustomer.getEmail());
    }

    @Test
    void testCustomerPhoneUpdate() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");
        customer = customerRepository.save(customer);

        customer.setPhone("5555555555");
        Customer updatedCustomer = customerRepository.save(customer);

        assertEquals("5555555555", updatedCustomer.getPhone());
    }
} 
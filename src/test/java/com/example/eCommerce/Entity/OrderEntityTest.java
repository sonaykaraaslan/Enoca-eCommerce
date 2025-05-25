package com.example.eCommerce.Entity;

import com.example.eCommerce.Repository.CustomerRepository;
import com.example.eCommerce.Repository.OrderRepository;
import com.example.eCommerce.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderEntityTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setPhone("1234567890");
        customer = customerRepository.save(customer);

        product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        product.setStock(10);
        product = productRepository.save(product);
    }

    @Test
    void testOrderCreation() {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(0.0);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals(customer.getId(), savedOrder.getCustomer().getId());
        assertEquals(0.0, savedOrder.getTotalAmount());
        assertEquals("PENDING", savedOrder.getStatus());
    }

    @Test
    void testOrderUpdate() {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(0.0);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        order.setTotalAmount(100.0);
        order.setStatus("COMPLETED");

        Order updatedOrder = orderRepository.save(order);

        assertEquals(100.0, updatedOrder.getTotalAmount());
        assertEquals("COMPLETED", updatedOrder.getStatus());
    }

    @Test
    void testOrderWithItems() {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(0.0);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(product.getPrice());

        order.getOrderItems().add(orderItem);
        order.setTotalAmount(product.getPrice() * 2);

        Order updatedOrder = orderRepository.save(order);

        assertEquals(200.0, updatedOrder.getTotalAmount());
        assertEquals(1, updatedOrder.getOrderItems().size());
    }

    @Test
    void testOrderStatusTransitions() {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(0.0);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        // PENDING -> PROCESSING
        order.setStatus("PROCESSING");
        Order processingOrder = orderRepository.save(order);
        assertEquals("PROCESSING", processingOrder.getStatus());

        // PROCESSING -> SHIPPED
        processingOrder.setStatus("SHIPPED");
        Order shippedOrder = orderRepository.save(processingOrder);
        assertEquals("SHIPPED", shippedOrder.getStatus());

        // SHIPPED -> DELIVERED
        shippedOrder.setStatus("DELIVERED");
        Order deliveredOrder = orderRepository.save(shippedOrder);
        assertEquals("DELIVERED", deliveredOrder.getStatus());
    }

    @Test
    void testOrderWithMultipleItems() {
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(0.0);
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        // İlk ürün
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setOrder(order);
        orderItem1.setProduct(product);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(product.getPrice());

        // İkinci ürün
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2 = productRepository.save(product2);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setOrder(order);
        orderItem2.setProduct(product2);
        orderItem2.setQuantity(3);
        orderItem2.setPrice(product2.getPrice());

        order.getOrderItems().add(orderItem1);
        order.getOrderItems().add(orderItem2);
        order.setTotalAmount((product.getPrice() * 2) + (product2.getPrice() * 3));

        Order updatedOrder = orderRepository.save(order);

        assertEquals(350.0, updatedOrder.getTotalAmount());
        assertEquals(2, updatedOrder.getOrderItems().size());
    }
} 
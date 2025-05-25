package com.example.eCommerce.Service;

import com.example.eCommerce.Entity.*;
import com.example.eCommerce.Repository.*;
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
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    private Customer customer;
    private Product product;
    private Cart cart;
    private Order order;

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
    void testCreateOrder() {
        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setTotalAmount(200.0);
        newOrder.setStatus("PENDING");

        Order savedOrder = orderService.createOrder(newOrder);

        assertNotNull(savedOrder.getId());
        assertEquals(customer.getId(), savedOrder.getCustomer().getId());
        assertEquals(200.0, savedOrder.getTotalAmount());
        assertEquals("PENDING", savedOrder.getStatus());
    }

    @Test
    void testGetOrderById() {
        Optional<Order> foundOrder = orderService.getOrderById(order.getId());

        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());
        assertEquals(customer.getId(), foundOrder.get().getCustomer().getId());
        assertEquals(150.0, foundOrder.get().getTotalAmount());
    }

    @Test
    void testGetAllOrders() {
        // İkinci sipariş ekle
        Order order2 = new Order();
        order2.setCustomer(customer);
        order2.setTotalAmount(250.0);
        order2.setStatus("PENDING");
        orderRepository.save(order2);

        List<Order> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getTotalAmount() == 150.0));
        assertTrue(orders.stream().anyMatch(o -> o.getTotalAmount() == 250.0));
    }

    @Test
    void testUpdateOrderStatus() {
        Order updatedOrder = orderService.updateOrderStatus(order.getId(), "COMPLETED");

        assertEquals("COMPLETED", updatedOrder.getStatus());
    }

    @Test
    void testGetCustomerOrders() {
        // İkinci sipariş ekle
        Order order2 = new Order();
        order2.setCustomer(customer);
        order2.setTotalAmount(250.0);
        order2.setStatus("PENDING");
        orderRepository.save(order2);

        List<Order> customerOrders = orderService.getCustomerOrders(customer.getId());

        assertEquals(2, customerOrders.size());
        assertTrue(customerOrders.stream().allMatch(o -> o.getCustomer().getId().equals(customer.getId())));
    }

    @Test
    void testOrderWithItems() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(product.getPrice());

        order.getOrderItems().add(orderItem);
        order.setTotalAmount(product.getPrice() * 2);

        Order updatedOrder = orderService.updateOrder(order.getId(), order);

        assertEquals(200.0, updatedOrder.getTotalAmount());
        assertEquals(1, updatedOrder.getOrderItems().size());
    }

    @Test
    void testOrderStatusValidation() {
        // Geçersiz durum
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(order.getId(), "INVALID_STATUS");
        });
    }

    @Test
    void testOrderTotalAmountValidation() {
        // Toplam tutar negatif olamaz
        order.setTotalAmount(-100.0);
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrder(order.getId(), order);
        });
    }
} 
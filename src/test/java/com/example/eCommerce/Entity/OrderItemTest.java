package com.example.eCommerce.Entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testOrderItemCreation() {
        OrderItem orderItem = new OrderItem();
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        
        Order order = new Order();
        
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(2);
        orderItem.setPrice(100.0);

        assertNotNull(orderItem);
        assertEquals("Test Product", orderItem.getProduct().getName());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(100.0, orderItem.getPrice());
    }

    @Test
    void testOrderItemQuantityUpdate() {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        
        orderItem.setQuantity(3);
        assertEquals(3, orderItem.getQuantity());
    }

    @Test
    void testOrderItemPriceUpdate() {
        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(100.0);
        
        orderItem.setPrice(150.0);
        assertEquals(150.0, orderItem.getPrice());
    }
} 
package com.example.eCommerce.Entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    @Test
    void testCartItemCreation() {
        CartItem cartItem = new CartItem();
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100.0);
        
        Cart cart = new Cart();
        
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(2);
        cartItem.setPrice(100.0);

        assertNotNull(cartItem);
        assertEquals("Test Product", cartItem.getProduct().getName());
        assertEquals(2, cartItem.getQuantity());
        assertEquals(100.0, cartItem.getPrice());
    }

    @Test
    void testCartItemQuantityUpdate() {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        
        cartItem.setQuantity(3);
        assertEquals(3, cartItem.getQuantity());
    }

    @Test
    void testCartItemPriceUpdate() {
        CartItem cartItem = new CartItem();
        cartItem.setPrice(100.0);
        
        cartItem.setPrice(150.0);
        assertEquals(150.0, cartItem.getPrice());
    }
} 
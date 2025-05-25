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
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer customer;
    private Product product;
    private Cart cart;

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
    }

    @Test
    void testCreateCart() {
        Cart newCart = new Cart();
        newCart.setCustomer(customer);
        newCart.setTotalAmount(0.0);

        Cart savedCart = cartService.createCart(newCart);

        assertNotNull(savedCart.getId());
        assertEquals(customer.getId(), savedCart.getCustomer().getId());
        assertEquals(0.0, savedCart.getTotalAmount());
    }

    @Test
    void testGetCartById() {
        Optional<Cart> foundCart = cartService.getCartById(cart.getId());

        assertTrue(foundCart.isPresent());
        assertEquals(cart.getId(), foundCart.get().getId());
        assertEquals(customer.getId(), foundCart.get().getCustomer().getId());
    }

    @Test
    void testGetCustomerCart() {
        Optional<Cart> customerCart = cartService.getCustomerCart(customer.getId());

        assertTrue(customerCart.isPresent());
        assertEquals(cart.getId(), customerCart.get().getId());
        assertEquals(customer.getId(), customerCart.get().getCustomer().getId());
    }

    @Test
    void testAddItemToCart() {
        Cart updatedCart = cartService.addItemToCart(cart.getId(), product.getId(), 2);

        assertEquals(200.0, updatedCart.getTotalAmount());
        assertEquals(1, updatedCart.getCartItems().size());
        assertEquals(2, updatedCart.getCartItems().get(0).getQuantity());
    }

    @Test
    void testUpdateCartItemQuantity() {
        // Önce ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // Miktarı güncelle
        Cart updatedCart = cartService.updateCartItemQuantity(cart.getId(), product.getId(), 3);

        assertEquals(300.0, updatedCart.getTotalAmount());
        assertEquals(1, updatedCart.getCartItems().size());
        assertEquals(3, updatedCart.getCartItems().get(0).getQuantity());
    }

    @Test
    void testRemoveItemFromCart() {
        // Önce ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // Ürünü kaldır
        Cart updatedCart = cartService.removeItemFromCart(cart.getId(), product.getId());

        assertEquals(0.0, updatedCart.getTotalAmount());
        assertTrue(updatedCart.getCartItems().isEmpty());
    }

    @Test
    void testClearCart() {
        // Önce ürün ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // Sepeti temizle
        Cart clearedCart = cartService.clearCart(cart.getId());

        assertEquals(0.0, clearedCart.getTotalAmount());
        assertTrue(clearedCart.getCartItems().isEmpty());
    }

    @Test
    void testCartItemQuantityValidation() {
        // Stoktan fazla ürün eklenemez
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItemToCart(cart.getId(), product.getId(), product.getStock() + 1);
        });

        // Negatif miktar eklenemez
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItemToCart(cart.getId(), product.getId(), -1);
        });
    }

    @Test
    void testCartWithMultipleItems() {
        // İkinci ürün ekle
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2 = productRepository.save(product2);

        // İlk ürünü ekle
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // İkinci ürünü ekle
        Cart updatedCart = cartService.addItemToCart(cart.getId(), product2.getId(), 3);

        assertEquals(350.0, updatedCart.getTotalAmount());
        assertEquals(2, updatedCart.getCartItems().size());
    }

    @Test
    void testCartTotalAmountCalculation() {
        // İkinci ürün ekle
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2 = productRepository.save(product2);

        // İlk ürünü ekle (2 adet)
        cartService.addItemToCart(cart.getId(), product.getId(), 2);

        // İkinci ürünü ekle (3 adet)
        Cart updatedCart = cartService.addItemToCart(cart.getId(), product2.getId(), 3);

        // Toplam tutar: (100 * 2) + (50 * 3) = 350
        assertEquals(350.0, updatedCart.getTotalAmount());
    }
} 
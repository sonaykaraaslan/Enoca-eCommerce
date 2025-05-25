package com.example.eCommerce.Entity;

import com.example.eCommerce.Repository.CartRepository;
import com.example.eCommerce.Repository.CustomerRepository;
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
class CartEntityTest {

    @Autowired
    private CartRepository cartRepository;

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
    void testCartCreation() {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setTotalAmount(0.0);

        Cart savedCart = cartRepository.save(cart);

        assertNotNull(savedCart.getId());
        assertEquals(customer.getId(), savedCart.getCustomer().getId());
        assertEquals(0.0, savedCart.getTotalAmount());
    }

    @Test
    void testCartUpdate() {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setTotalAmount(0.0);
        cart = cartRepository.save(cart);

        cart.setTotalAmount(100.0);
        Cart updatedCart = cartRepository.save(cart);

        assertEquals(100.0, updatedCart.getTotalAmount());
    }

    @Test
    void testCartWithItems() {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setTotalAmount(0.0);
        cart = cartRepository.save(cart);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(product.getPrice());

        cart.getCartItems().add(cartItem);
        cart.setTotalAmount(product.getPrice() * 2);

        Cart updatedCart = cartRepository.save(cart);

        assertEquals(200.0, updatedCart.getTotalAmount());
        assertEquals(1, updatedCart.getCartItems().size());
    }

    @Test
    void testCartTotalAmountCalculation() {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setTotalAmount(0.0);
        cart = cartRepository.save(cart);

        // İlk ürün
        CartItem cartItem1 = new CartItem();
        cartItem1.setCart(cart);
        cartItem1.setProduct(product);
        cartItem1.setQuantity(2);
        cartItem1.setPrice(product.getPrice());

        // İkinci ürün
        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(50.0);
        product2.setStock(5);
        product2 = productRepository.save(product2);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCart(cart);
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(3);
        cartItem2.setPrice(product2.getPrice());

        cart.getCartItems().add(cartItem1);
        cart.getCartItems().add(cartItem2);
        cart.setTotalAmount((product.getPrice() * 2) + (product2.getPrice() * 3));

        Cart updatedCart = cartRepository.save(cart);

        assertEquals(350.0, updatedCart.getTotalAmount());
        assertEquals(2, updatedCart.getCartItems().size());
    }
} 
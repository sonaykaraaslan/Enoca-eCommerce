package com.example.eCommerce.Service;

import com.example.eCommerce.Entity.*;
import com.example.eCommerce.Repository.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(CustomerRepository customerRepository,
                       ProductRepository productRepository,
                       CartRepository cartRepository,
                       CartItemRepository cartItemRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    //  Sepeti getir
    public Cart getCartByCustomerId(Long customerId) {
        return customerRepository.findById(customerId)
                .map(Customer::getCart)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    //  Sepete ürün ekle
    public Cart addProductToCart(Long customerId, Long productId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stok yetersiz!");
        }

        Cart cart = customer.getCart();

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalPrice(cartItem.getQuantity() * product.getPrice());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setTotalPrice(quantity * product.getPrice());
            cart.getItems().add(cartItem);
        }

        // Sepet toplamını güncelle
        double newTotal = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalPrice(newTotal);

        cartRepository.save(cart);
        return cart;
    }

    //  Sepetten ürün çıkar
    public Cart removeProductFromCart(Long customerId, Long productId) {
        Cart cart = getCartByCustomerId(customerId);

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        // Güncel fiyatı hesapla
        double newTotal = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalPrice(newTotal);

        return cartRepository.save(cart);
    }

    //  Sepeti boşalt
    public Cart emptyCart(Long customerId) {
        Cart cart = getCartByCustomerId(customerId);

        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        return cartRepository.save(cart);
    }
}

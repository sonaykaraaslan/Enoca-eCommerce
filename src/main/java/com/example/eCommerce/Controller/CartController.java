package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Cart;
import com.example.eCommerce.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    //  Sepeti getir
    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long customerId) {
        Cart cart = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cart);
    }

    //  Sepete ürün ekle
    @PostMapping("/{customerId}/add")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long customerId,
                                                 @RequestParam Long productId,
                                                 @RequestParam int quantity) {
        Cart updatedCart = cartService.addProductToCart(customerId, productId, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    //  Sepetten ürün çıkar
    @DeleteMapping("/{customerId}/remove")
    public ResponseEntity<Cart> removeProductFromCart(@PathVariable Long customerId,
                                                      @RequestParam Long productId) {
        Cart updatedCart = cartService.removeProductFromCart(customerId, productId);
        return ResponseEntity.ok(updatedCart);
    }

    //  Sepeti boşalt
    @DeleteMapping("/{customerId}/empty")
    public ResponseEntity<Cart> emptyCart(@PathVariable Long customerId) {
        Cart emptiedCart = cartService.emptyCart(customerId);
        return ResponseEntity.ok(emptiedCart);
    }
}

package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Product;
import com.example.eCommerce.Service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestParam String name,
                                                 @RequestParam String description,
                                                 @RequestParam double price,
                                                 @RequestParam int stock) {
        Product product = productService.createProduct(name, description, price, stock);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);
        return productOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Ürün güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestParam String name,
                                                 @RequestParam String description,
                                                 @RequestParam double price,
                                                 @RequestParam int stock,
                                                 @RequestParam boolean active) {
        Product updated = productService.updateProduct(id, name, description, price, stock, active);
        return ResponseEntity.ok(updated);
    }

    //  Ürün silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

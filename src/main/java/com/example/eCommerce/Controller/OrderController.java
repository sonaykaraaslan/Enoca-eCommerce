package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Order;
import com.example.eCommerce.Service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Sipariş oluştur
    @PostMapping("/{customerId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long customerId) {
        Order order = orderService.placeOrder(customerId);
        return ResponseEntity.ok(order);
    }

    // Sipariş koduna göre getir
    @GetMapping("/code/{code}")
    public ResponseEntity<Order> getOrderByCode(@PathVariable String code) {
        Order order = orderService.getOrderByCode(code);
        return ResponseEntity.ok(order);
    }

    // Müşterinin tüm siparişlerini getir
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getAllOrdersForCustomer(@PathVariable Long customerId) {
        List<Order> orders = orderService.getAllOrdersForCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
}

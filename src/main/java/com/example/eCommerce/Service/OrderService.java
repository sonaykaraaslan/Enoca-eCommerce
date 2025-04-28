package com.example.eCommerce.Service;

import com.example.eCommerce.Entity.*;
import com.example.eCommerce.Repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public OrderService(CustomerRepository customerRepository,
                        OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    //  Siparişi oluştur
    public Order placeOrder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = customer.getCart();

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Sepet boş. Sipariş oluşturulamaz.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stok yetersiz: " + product.getName());
            }

            // stok azalt
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(product.getName());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(product.getPrice() * cartItem.getQuantity());

            orderItems.add(orderItem);
            totalPrice += orderItem.getTotalPrice();
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        order.setOrderCode(UUID.randomUUID().toString());

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return orderRepository.save(order);
    }

    //  Kod ile sipariş getir
    public Order getOrderByCode(String code) {
        return orderRepository.findByOrderCode(code)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı: " + code));
    }

    //  Müşterinin tüm siparişlerini getir
    public List<Order> getAllOrdersForCustomer(Long customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }
}

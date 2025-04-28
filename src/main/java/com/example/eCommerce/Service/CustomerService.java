package com.example.eCommerce.Service;

import com.example.eCommerce.Entity.Cart;
import com.example.eCommerce.Entity.Customer;
import com.example.eCommerce.Repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(String name, String email) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);

        // Yeni müşteri oluşturulurken boş bir sepet de atanır
        Cart cart = new Cart();
        customer.setCart(cart);

        return customerRepository.save(customer);
    }
}

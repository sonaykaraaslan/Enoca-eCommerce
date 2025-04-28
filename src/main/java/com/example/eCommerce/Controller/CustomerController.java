package com.example.eCommerce.Controller;

import com.example.eCommerce.Entity.Customer;
import com.example.eCommerce.Service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestParam String name,
                                                @RequestParam String email) {
        Customer createdCustomer = customerService.addCustomer(name, email);
        return ResponseEntity.ok(createdCustomer);
    }
}

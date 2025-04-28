package com.example.eCommerce.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product extends BaseEntity {

    private String name;
    private String description;
    private double price;
    private int stock;

    // Ürün silinmedi ama satıştan kaldırıldı gibi bir durum için
    private boolean active = true;
}

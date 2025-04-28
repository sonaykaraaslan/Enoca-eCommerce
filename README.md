# 🛍️eCommerce Backend Project

Spring Boot ile geliştirilen bu proje, temel e-ticaret işlevlerini sağlayan bir backend sistemidir. Müşteri, ürün, sepet ve sipariş işlemleri veritabanı ile entegre biçimde çalışır. MySQL veritabanı kullanılmış olup, tablolar JPA üzerinden otomatik olarak oluşturulmaktadır.

## 🎯Özellikler

- Müşteri yönetimi
- Ürün ekleme, stok takibi
- Sepet işlemleri ve toplam fiyat hesaplama
- Sipariş oluşturma, geçmiş siparişleri görüntüleme
- Ürün fiyat geçmişi saklama
- Stok kontrolü ve stok yetersizliği önleme

## Teknolojiler

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Lombok
- Postman (API testleri için)
- IntelliJ IDEA (IDE)

## API Örnekleri
### ➕ Müşteri Oluştur
POST /api/customers?name=Sonay&email=sonay@example.com

## ⚙️ Kurulum

- MySQL üzerinde `ecommerce_db` isminde veritabanı oluşturulmalı.
- `application.properties` dosyasında bağlantı bilgileri düzenlenmeli.
- Uygulama çalıştırıldığında tablolar otomatik oluşur.
 
## 👨‍💻 Geliştirici

**Sonay Karaaslan**  
Bilgisayar Mühendisliği 4. Sınıf Öğrencisi  


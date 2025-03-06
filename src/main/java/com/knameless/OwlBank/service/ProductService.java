package com.knameless.OwlBank.service;

import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        Client client = product.getClient();
        if (client == null) {
            throw new RuntimeException("The product must be linked to a client");
        }
        if (productRepository.existsByNumber(product.getNumber())) {
            throw new RuntimeException("The account number must be unique");
        }
        if (product.getType().equals("SAVINGS") && product.getBalance() < 0) {
            throw new RuntimeException("A savings account cannot have a negative balance");
        }
        product.setCreationDate(LocalDate.now());
        return productRepository.save(product);
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(product -> {
            product.setType(updatedProduct.getType());
            product.setBalance(updatedProduct.getBalance());
            product.setStatus(updatedProduct.getStatus());
            product.setModificationDate(LocalDate.now());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getBalance() > 0) {
            throw new RuntimeException("Only accounts with a zero balance can be deleted");
        }
        productRepository.delete(product);
    }
}
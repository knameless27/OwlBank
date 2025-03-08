package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.ProductDTO;
import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.repository.ClientRepository;
import com.knameless.OwlBank.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        boolean clientExists = clientRepository.existsById(productDTO.clientId());

        if (!clientExists) throw new RuntimeException("The client doesn't exist!");

        if (productDTO.type().equals("SAVINGS") && productDTO.balance() < 0)
            throw new RuntimeException("A savings account cannot have a negative balance");

        Product product = new Product();

        product.setClient(clientRepository.findById(productDTO.clientId()).orElseThrow(() ->
                new RuntimeException("Client not found!")));
        product.setType(productDTO.type());
        product.setNumber(productDTO.number());
        product.setBalance(productDTO.balance());
        product.setGmfExempt(productDTO.gmfExempt());
        product.setStatus(productDTO.status());
        product.setCreationDate(LocalDate.now());

        productRepository.save(product);
        return new ProductDTO(product.getId(), product.getType(), product.getNumber(), product.getStatus(), product.getBalance(), product.isGmfExempt(), product.getClient().getId(), product.getCreationDate());
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
package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.ProductDTO;
import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.enums.AccountType;
import com.knameless.OwlBank.enums.StatusAccount;
import com.knameless.OwlBank.repository.ClientRepository;
import com.knameless.OwlBank.repository.ProductRepository;
import com.knameless.OwlBank.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

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

        if (productDTO.type().equals(AccountType.SAVINGS) && productDTO.balance() < 0)
            throw new RuntimeException("A savings account cannot have a negative balance");

        Product product = new Product();
        product.setClient(clientRepository.findById(productDTO.clientId()).orElseThrow(() ->
                new RuntimeException("Client not found!")));
        product.setType(productDTO.type());
        product.setNumber(generateAccountNumber(productDTO.type().toString()));
        product.setBalance(productDTO.balance());
        product.setGmfExempt(productDTO.gmfExempt());
        product.setCreationDate(LocalDate.now());
        product.setModificationDate(LocalDate.now());

        if (productDTO.type().equals(AccountType.SAVINGS))
            product.setStatus(StatusAccount.ACTIVE);
        else
            product.setStatus(productDTO.status());

        productRepository.save(product);
        return new ProductDTO(product.getId(), product.getType(), product.getNumber(), product.getStatus(), product.getBalance(), product.isGmfExempt(), product.getClient().getId(), product.getCreationDate());
    }

    private String generateAccountNumber(String type) {
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);
        return (type.equals("SAVINGS") ? "53" : "33") + randomNumber;
    }

    public List<ProductDTO> getProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getType(),
                        product.getNumber(),
                        product.getStatus(),
                        product.getBalance(),
                        product.isGmfExempt(),
                        product.getClient().getId(),
                        product.getCreationDate()
                ))
                .toList();
    }


    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product no found!"));

        return new ProductDTO(
                product.getId(),
                product.getType(),
                product.getNumber(),
                product.getStatus(),
                product.getBalance(),
                product.isGmfExempt(),
                product.getClient().getId(),
                product.getCreationDate()
        );
    }

    public ProductDTO updateProduct(Long id, Product updatedProduct) {
        if(updatedProduct.getBalance() < 0)
            throw new RuntimeException("This transaction can't be done by insufficient fonds!");

        Product prod = productRepository.findById(id).map(product -> {
            product.setBalance(updatedProduct.getBalance());
            product.setStatus(updatedProduct.getStatus());
            product.setModificationDate(LocalDate.now());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found"));

        return new ProductDTO(
                prod.getId(),
                prod.getType(),
                prod.getNumber(),
                prod.getStatus(),
                prod.getBalance(),
                prod.isGmfExempt(),
                prod.getClient().getId(),
                prod.getCreationDate()
        );
    }

    public ResponseEntity<ApiResponse> deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getBalance() > 0)
            throw new RuntimeException("Only accounts with a zero balance can be deleted");

        productRepository.delete(product);

        return ResponseEntity.ok(ApiResponse.success(""));
    }
}

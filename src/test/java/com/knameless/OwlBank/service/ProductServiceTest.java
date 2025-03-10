package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.ProductDTO;
import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.enums.AccountType;
import com.knameless.OwlBank.enums.StatusAccount;
import com.knameless.OwlBank.repository.ClientRepository;
import com.knameless.OwlBank.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Client client;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setClient(client);
        product.setType(AccountType.SAVINGS);
        product.setNumber("5312345678");
        product.setBalance(1000.0);
        product.setGmfExempt(false);
        product.setStatus(StatusAccount.ACTIVE);
        product.setCreationDate(LocalDate.now());

        productDTO = new ProductDTO(
                1L,
                AccountType.SAVINGS,
                "5312345678",
                StatusAccount.ACTIVE,
                1000.0,
                false,
                1L,
                LocalDate.now()
        );
    }

    @Test
    void createProduct_ShouldCreateNewProduct_WhenClientExists() {
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals(productDTO.clientId(), result.clientId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_ShouldThrowException_WhenClientDoesNotExist() {
        when(clientRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(productDTO);
        });

        assertEquals("The client doesn't exist!", exception.getMessage());
    }

    @Test
    void getProductById_ShouldReturnProductDTO_WhenProductExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(1L);
        });

        assertEquals("Product no found!", exception.getMessage());
    }
}
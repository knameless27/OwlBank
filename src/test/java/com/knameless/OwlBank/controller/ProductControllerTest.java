package com.knameless.OwlBank.controller;

import com.knameless.OwlBank.dto.ProductDTO;
import com.knameless.OwlBank.enums.AccountType;
import com.knameless.OwlBank.enums.StatusAccount;
import com.knameless.OwlBank.service.ProductService;
import com.knameless.OwlBank.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Creación del ProductDTO con un clientId en lugar de un objeto ClientDTO
        productDTO = new ProductDTO(
                1L,
                AccountType.SAVINGS,
                "123456789",
                StatusAccount.ACTIVE,
                1000.0,
                true,
                10L, // Solo pasamos el ID del cliente
                LocalDate.of(2024, 3, 10)
        );
    }

    @Test
    void createProduct() {
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.createProduct(productDTO);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(AccountType.SAVINGS, response.getBody().type());
        assertEquals("123456789", response.getBody().number());
        assertEquals(StatusAccount.ACTIVE, response.getBody().status());
        assertEquals(1000.0, response.getBody().balance());
        assertTrue(response.getBody().gmfExempt());
        assertEquals(10L, response.getBody().clientId()); // Accedemos al ID del cliente, no a un objeto

        verify(productService, times(1)).createProduct(any(ProductDTO.class));
    }

    @Test
    void getProducts() {
        ProductDTO productDTO2 = new ProductDTO(
                2L,
                AccountType.CHECKING,
                "987654321",
                StatusAccount.INACTIVE,
                500.0,
                false,
                15L, // Otro clientId
                LocalDate.of(2024, 2, 15)
        );

        List<ProductDTO> productList = List.of(productDTO, productDTO2);
        when(productService.getProducts()).thenReturn(productList);

        ResponseEntity<List<ProductDTO>> response = productController.getProducts();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("123456789", response.getBody().get(0).number());
        assertEquals(10L, response.getBody().get(0).clientId()); // Accedemos correctamente al clientId
        assertEquals("987654321", response.getBody().get(1).number());
        assertEquals(15L, response.getBody().get(1).clientId());

        verify(productService, times(1)).getProducts();
    }

    @Test
    void getProductById() {
        Long id = 1L;
        when(productService.getProductById(id)).thenReturn(productDTO);

        ResponseEntity<ProductDTO> response = productController.getProductById(id);

        assertNotNull(response);
        assertEquals(1L, response.getBody().id());
        assertEquals("123456789", response.getBody().number());
        assertEquals(10L, response.getBody().clientId());

        verify(productService, times(1)).getProductById(id);
    }

    @Test
    void updateProduct() {
        Long id = 1L;
        ProductDTO updatedProductDTO = new ProductDTO(
                id,
                AccountType.CHECKING,
                "111111111",
                StatusAccount.ACTIVE,
                1500.0,
                false,
                20L, // Nuevo clientId
                LocalDate.of(2024, 3, 11)
        );

        when(productService.updateProduct(eq(id), any())).thenReturn(updatedProductDTO);

        ResponseEntity<ProductDTO> response = productController.updateProduct(id, new com.knameless.OwlBank.entity.Product());

        assertNotNull(response);
        assertEquals(1L, response.getBody().id());
        assertEquals("111111111", response.getBody().number());
        assertEquals(1500.0, response.getBody().balance());
        assertEquals(20L, response.getBody().clientId()); // Nuevo clientId

        verify(productService, times(1)).updateProduct(eq(id), any());
    }

    @Test
    void deleteProduct() {
        Long id = 1L;
        ApiResponse apiResponse = new ApiResponse(true, "Product deleted successfully", "");
        ResponseEntity<ApiResponse> expectedResponse = ResponseEntity.ok(apiResponse);
        when(productService.deleteProduct(id)).thenReturn(expectedResponse);

        ResponseEntity<ApiResponse> response = productController.deleteProduct(id);

        assertNotNull(response);
        assertEquals("Product deleted successfully", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());

        verify(productService, times(1)).deleteProduct(id);
    }
}

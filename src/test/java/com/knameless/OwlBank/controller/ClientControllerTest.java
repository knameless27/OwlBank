package com.knameless.OwlBank.controller;

import com.knameless.OwlBank.dto.ClientDTO;
import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.enums.IdentificationType;
import com.knameless.OwlBank.service.ClientService;
import com.knameless.OwlBank.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    void testCreateClient() {
        Client client = new Client();
        client.setId(1L);
        client.setNames("John");
        client.setLastNames("Doe");
        client.setEmail("john.doe@example.com");
        client.setDob(LocalDate.of(1990, 1, 1));

        when(clientService.createClient(any(Client.class))).thenReturn(client);

        ResponseEntity<Client> response = clientController.createClient(client);
        assertNotNull(response);
        assertEquals(client, response.getBody());
    }

    @Test
    void testGetClients() {
        when(clientService.getClients()).thenReturn(Collections.emptyList());
        ResponseEntity<List<ClientDTO>> response = clientController.getClients();
        assertNotNull(response);
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetClientById() {
        ClientDTO clientDTO = new ClientDTO(1L, IdentificationType.PASSPORT, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), Collections.emptyList());
        when(clientService.getClientById(1L)).thenReturn(clientDTO);

        ResponseEntity<ClientDTO> response = clientController.getClientById(1L);
        assertNotNull(response);
        assertEquals(clientDTO, response.getBody());
    }

    @Test
    void testUpdateClient() {
        Client client = new Client();
        client.setId(1L);
        client.setNames("John");
        client.setLastNames("Doe");
        client.setEmail("john.doe@example.com");
        client.setDob(LocalDate.of(1990, 1, 1));

        ClientDTO clientDTO = new ClientDTO(1L, IdentificationType.CC, "John", "Doe", "john.doe@example.com", LocalDate.of(1990, 1, 1), Collections.emptyList());
        when(clientService.updateClient(1L, clientDTO)).thenReturn(client);

        ResponseEntity<Client> response = clientController.updateClient(1L, clientDTO);
        assertNotNull(response);
        assertEquals(client, response.getBody());
    }

    @Test
    void testDeleteClient() {
        ApiResponse apiResponse = ApiResponse.success("Deleted");
        when(clientService.deleteClient(1L)).thenReturn(ResponseEntity.ok(apiResponse));

        ResponseEntity<ApiResponse> response = clientController.deleteClient(1L);
        assertNotNull(response);
        assertEquals(apiResponse, response.getBody());
    }
}

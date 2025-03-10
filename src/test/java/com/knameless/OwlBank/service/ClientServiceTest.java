package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.ClientDTO;
import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.enums.IdentificationType;
import com.knameless.OwlBank.repository.ClientRepository;
import com.knameless.OwlBank.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setNames("John");
        client.setLastNames("Doe");
        client.setEmail("johndoe@example.com");
        client.setDob(LocalDate.of(1990, 1, 1));
        client.setCreationDate(LocalDate.now());
    }

    @Test
    void testCreateClient_Success() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client savedClient = clientService.createClient(client);

        assertNotNull(savedClient);
        assertEquals("John", savedClient.getNames());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testCreateClient_FailsUnderage() {
        client.setDob(LocalDate.now().minusYears(17));

        Exception exception = assertThrows(RuntimeException.class, () -> clientService.createClient(client));
        assertEquals("The user had to be older than 18!", exception.getMessage());
    }

    @Test
    void testGetClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));

        List<ClientDTO> clients = clientService.getClients();

        assertFalse(clients.isEmpty());
        assertEquals(1, clients.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testGetClientById_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientDTO foundClient = clientService.getClientById(1L);

        assertNotNull(foundClient);
        assertEquals("John", foundClient.names());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClientById_NotFound() {
        when(clientRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> clientService.getClientById(2L));
        assertEquals("Client doesn't exist!", exception.getMessage());
    }

    @Test
    void testUpdateClient_Success() {
        ClientDTO updatedClient = new ClientDTO(1L, IdentificationType.CC, "Jane", "Doe", "janedoe@example.com", LocalDate.of(1995, 5, 15), List.of());

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.updateClient(1L, updatedClient);

        assertNotNull(result);
        assertEquals("Jane", result.getNames());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testUpdateClient_InvalidEmail() {
        ClientDTO updatedClient = new ClientDTO(1L, IdentificationType.PASSPORT, "Jane", "Doe", "invalid-email", LocalDate.of(1995, 5, 15), List.of());

        Exception exception = assertThrows(RuntimeException.class, () -> clientService.updateClient(1L, updatedClient));
        assertEquals("the email is not valid!", exception.getMessage());
    }

    @Test
    void testDeleteClient_Success() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.deleteClient(1L);

        verify(productRepository, times(1)).deleteAll(client.getProducts());
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteClient_NotFound() {
        when(clientRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> clientService.deleteClient(2L));
        assertEquals("Client no found!", exception.getMessage());
    }
}

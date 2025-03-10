package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.ClientDTO;
import com.knameless.OwlBank.dto.ProductDTO;
import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.repository.ClientRepository;
import com.knameless.OwlBank.repository.ProductRepository;
import com.knameless.OwlBank.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.knameless.OwlBank.utils.EmailValidator.isValidEmail;

@Service
public class ClientService {
    @Autowired
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository, ProductRepository productRepository) {
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
    }

    public Client createClient(Client client) {
        if (LocalDate.now().minusYears(18).isBefore(client.getDob()))
            throw new RuntimeException("The user had to be older than 18!");

        client.setCreationDate(LocalDate.now());
        return clientRepository.save(client);
    }

    public List<ClientDTO> getClients() {
        List<Client> clients = clientRepository.findAll();

        return clients.stream().map(client -> {
            if(client.getProducts() == null || client.getProducts().isEmpty())
                return new ClientDTO(client.getId(),client.getIdType(), client.getNames(), client.getLastNames(), client.getEmail(), client.getDob(), null);

            List<ProductDTO> productDTOs = client.getProducts().stream()
                    .map(p -> new ProductDTO(p.getId(), p.getType(), p.getNumber(), p.getStatus(), p.getBalance(), p.isGmfExempt(), p.getClient().getId(), p.getCreationDate()))
                    .toList();

            return new ClientDTO(client.getId(),client.getIdType(), client.getNames(), client.getLastNames(), client.getEmail(), client.getDob(), productDTOs);
        }).toList();
    }

    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client doesn't exist!"));

        if(client.getProducts() == null || client.getProducts().isEmpty())
            return new ClientDTO(client.getId(),client.getIdType(), client.getNames(), client.getLastNames(), client.getEmail(), client.getDob(), null);

        List<ProductDTO> productDTOs = client.getProducts().stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getType(),
                        product.getNumber(),
                        product.getStatus(),
                        product.getBalance(),
                        product.isGmfExempt(),
                        null,
                        product.getCreationDate()
                ))
                .toList();

        return new ClientDTO(client.getId(),client.getIdType(), client.getNames(), client.getLastNames(), client.getEmail(), client.getDob(), productDTOs);
    }

    public Client updateClient(Long id, ClientDTO updatedClient) {
        if(!isValidEmail(updatedClient.email())) throw new RuntimeException("the email is not valid!");

        return clientRepository.findById(id).map(client -> {
            client.setNames(updatedClient.names());
            client.setLastNames(updatedClient.lastNames());
            client.setEmail(updatedClient.email());
            client.setModificationDate(LocalDate.now());
            return clientRepository.save(client);
        }).orElseThrow(() -> new RuntimeException("Client not found!"));
    }

    public ResponseEntity<ApiResponse> deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client no found!"));

        productRepository.deleteAll(client.getProducts());

        clientRepository.delete(client);

        return ResponseEntity.ok(ApiResponse.success(""));
    }
}
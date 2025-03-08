package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.ClientDTO;
import com.knameless.OwlBank.dto.ProductDTO;
import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.repository.ClientRepository;
import com.knameless.OwlBank.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.knameless.OwlBank.utils.EmailValidator.isValidEmail;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    private ProductRepository productRepository;

    public Client createClient(Client client) {
        if (LocalDate.now().minusYears(18).isBefore(client.getDob()))
            throw new RuntimeException("The user had to be older than 18!");

        client.setCreationDate(LocalDate.now());
        return clientRepository.save(client);
    }

    public List<ClientDTO> getClients() {
        List<Client> clients = clientRepository.findAll();

        return clients.stream().map(client -> {
            List<ProductDTO> productDTOs = client.getProducts().stream()
                    .map(p -> new ProductDTO(p.getId(), p.getType(), p.getNumber(), p.getStatus(), p.getBalance(), p.isGmfExempt(), p.getClient().getId(), p.getCreationDate()))
                    .toList();

            return new ClientDTO(client.getId(),client.getIdType(), client.getNames(), client.getLastNames(), client.getEmail(), client.getDob(), productDTOs);
        }).toList();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
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

    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client no found!"));
        if (!client.getProducts().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un client con productos asociados");
        }
        clientRepository.delete(client);
    }
}
package com.knameless.OwlBank.service;

import com.knameless.OwlBank.entity.Client;
import com.knameless.OwlBank.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public Client createClient(Client client) {
        if (clientRepository.existsByIdNumber(client.getIdNumber())) {
            throw new RuntimeException("El client ya existe");
        }
        if (LocalDate.now().minusYears(18).isBefore(client.getDob())) {
            throw new RuntimeException("El client debe ser mayor de edad");
        }
        client.setCreationDate(LocalDate.now());
        return clientRepository.save(client);
    }

    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client updateClient(Long id, Client clientActualizado) {
        return clientRepository.findById(id).map(client -> {
            client.setNames(clientActualizado.getNames());
            client.setLastNames(clientActualizado.getLastNames());
            client.setEmail(clientActualizado.getEmail());
            client.setModificationDate(LocalDate.now());
            return clientRepository.save(client);
        }).orElseThrow(() -> new RuntimeException("Client no encontrado"));
    }

    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client no encontrado"));
        if (!client.getProducts().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un client con productos asociados");
        }
        clientRepository.delete(client);
    }
}
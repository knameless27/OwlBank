package com.knameless.OwlBank.repository;

import com.knameless.OwlBank.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByIdNumber(String idNumber);
}

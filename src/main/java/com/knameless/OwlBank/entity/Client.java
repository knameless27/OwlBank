package com.knameless.OwlBank.entity;

import com.knameless.OwlBank.enums.IdentificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private IdentificationType idType;

    @Column(nullable = false, unique = true)
    private String idNumber;

    @Column(nullable = false, length = 50)
    @Size(min=2, message="The minimum length for names is 2")
    private String names;

    @Column(nullable = false, length = 50)
    @Size(min=2, message="The minimum length for Last names is 2")
    private String lastNames;

    @Column(nullable = false, unique = true)
    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    private LocalDate modificationDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}
package com.knameless.OwlBank.entity;
import jakarta.persistence.*;
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
    private String idType;

    @Column(nullable = false, unique = true)
    private String idNumber;

    @Column(nullable = false, length = 2)
    private String names;

    @Column(nullable = false, length = 2)
    private String lastNames;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    private LocalDate modificationDate;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}
package com.knameless.OwlBank.dto;

import com.knameless.OwlBank.enums.IdentificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record ClientDTO(
        Long id,

        IdentificationType idType,

        @Size(min = 2, message = "The minimum length for names is 2")
        String names,

        @Size(min = 2, message = "The minimum length for last names is 2")
        String lastNames,

        @Email(message = "Invalid email format")
        String email,

        LocalDate dob,

        List<ProductDTO> products
) {}

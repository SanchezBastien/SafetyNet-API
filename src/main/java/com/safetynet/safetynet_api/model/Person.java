package com.safetynet.safetynet_api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class Person {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;

    @NotBlank(message = "La ville est obligatoire")
    private String city;

    @Pattern(regexp = "\\d{5}", message = "Le code postal doit comporter 5 chiffres")
    private String zip;

    @Pattern(regexp = "^[0-9\\-+() ]+$", message = "Numéro de téléphone invalide")
    private String phone;

    @Email(message = "Email invalide")
    private String email;
}

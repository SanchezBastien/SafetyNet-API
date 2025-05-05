package com.safetynet.safetynet_api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MedicalRecord {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    private String birthdate;

    private List<String> medications;

    private List<String> allergies;
}

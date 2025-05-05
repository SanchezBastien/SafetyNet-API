package com.safetynet.safetynet_api.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Firestation {

    @NotBlank(message = "L'addresse est obligatoire")
    private String address;

    @Min(value = 1, message = "Le numéro de station doit être supérieur ou égal à 1")
    private int station;
}

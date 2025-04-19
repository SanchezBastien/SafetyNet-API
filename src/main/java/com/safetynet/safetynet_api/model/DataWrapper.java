package com.safetynet.safetynet_api.model;

import lombok.Data;

import java.util.List;

@Data
public class DataWrapper {
    private List<Person> persons;
    private List<Firestation> firestations;
    private List<MedicalRecord> medicalrecords;
}

//Le JSON contient un objet global avec 3 tableaux (persons, firestations, medicalrecords)
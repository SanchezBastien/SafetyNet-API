package com.safetynet.safetynet_api.model;

import lombok.Data;
import java.util.List;

@Data
public class PersonInfoResponse {
    private String firstName;
    private String lastName;
    private String address;
    private int age;
    private String email;
    private List<String> medications;
    private List<String> allergies;

    public PersonInfoResponse() {}

    public PersonInfoResponse(String firstName, String lastName, String address, int age, String email,
                              List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.age = age;
        this.email = email;
        this.medications = medications;
        this.allergies = allergies;
    }
}

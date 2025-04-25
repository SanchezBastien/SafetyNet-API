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
}

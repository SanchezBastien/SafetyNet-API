package com.safetynet.safetynet_api.model;

import lombok.Data;
import java.util.List;

@Data
public class FireAddressResponse {
    private int stationNumber;
    private List<Resident> residents;

    @Data
    public static class Resident {
        private String firstName;
        private String lastName;
        private String phone;
        private int age;
        private List<String> medications;
        private List<String> allergies;
    }
}

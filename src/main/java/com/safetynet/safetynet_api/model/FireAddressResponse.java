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

        public Resident() {}

        public Resident(String firstName, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.age = age;
            this.medications = medications;
            this.allergies = allergies;
        }
    }

    public FireAddressResponse() {}

    public FireAddressResponse(int stationNumber, List<Resident> residents) {
        this.stationNumber = stationNumber;
        this.residents = residents;
    }
}
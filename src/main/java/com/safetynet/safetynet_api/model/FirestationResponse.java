package com.safetynet.safetynet_api.model;

import lombok.Data;
import java.util.List;

@Data
public class FirestationResponse {
    private List<PersonInfo> persons;
    private int numberOfAdults;
    private int numberOfChildren;

    @Data
    public static class PersonInfo {
        private String firstName;
        private String lastName;
        private String address;
        private String phone;

        public PersonInfo() {}

        public PersonInfo(String firstName, String lastName, String address, String phone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.phone = phone;
        }
    }

    public FirestationResponse() {}

    public FirestationResponse(List<PersonInfo> persons, int numberOfAdults, int numberOfChildren) {
        this.persons = persons;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }
}


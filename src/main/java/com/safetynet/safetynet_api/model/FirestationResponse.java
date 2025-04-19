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
    }
}

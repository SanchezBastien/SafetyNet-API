package com.safetynet.safetynet_api.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class FloodResponse {
    private Map<String, List<HouseholdMember>> households;

    @Data
    public static class HouseholdMember {
        private String firstName;
        private String lastName;
        private String phone;
        private int age;
        private List<String> medications;
        private List<String> allergies;

        public HouseholdMember() {}

        public HouseholdMember(String firstName, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.age = age;
            this.medications = medications;
            this.allergies = allergies;
        }
    }

    public FloodResponse() {}

    public FloodResponse(Map<String, List<HouseholdMember>> households) {
        this.households = households;
    }
}
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
    }
}

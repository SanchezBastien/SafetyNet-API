package com.safetynet.safetynet_api.model;

import lombok.Data;
import java.util.List;

@Data
public class ChildAlertResponse {
    private List<Child> children;
    private List<HouseholdMember> householdMembers;

    @Data
    public static class Child {
        private String firstName;
        private String lastName;
        private int age;
    }

    @Data
    public static class HouseholdMember {
        private String firstName;
        private String lastName;
    }
}

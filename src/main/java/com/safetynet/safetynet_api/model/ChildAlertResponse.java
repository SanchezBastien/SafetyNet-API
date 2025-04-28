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

        public Child() {}

        public Child(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }

    @Data
    public static class HouseholdMember {
        private String firstName;
        private String lastName;

        public HouseholdMember() {}

        public HouseholdMember(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public ChildAlertResponse() {}

    public ChildAlertResponse(List<Child> children, List<HouseholdMember> householdMembers) {
        this.children = children;
        this.householdMembers = householdMembers;
    }
}

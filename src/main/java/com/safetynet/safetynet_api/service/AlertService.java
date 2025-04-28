package com.safetynet.safetynet_api.service;

import com.safetynet.safetynet_api.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final DataLoaderService dataLoaderService;

    public AlertService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public int getAgeByName(String firstName, String lastName) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        Optional<MedicalRecord> record = data.getMedicalrecords().stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(firstName)
                        && m.getLastName().equalsIgnoreCase(lastName))
                .findFirst();

        if (record.isPresent()) {
            String birthdate = record.get().getBirthdate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birth = LocalDate.parse(birthdate, formatter);
            return Period.between(birth, LocalDate.now()).getYears();
        }
        return 0;
    }

    public List<Person> getPersonsByStationNumber(int stationNumber) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        List<String> addresses = data.getFirestations().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        return data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());
    }

    public FirestationResponse getFirestationResponse(int stationNumber) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        List<String> addresses = data.getFirestations().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        List<Person> persons = data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());

        List<FirestationResponse.PersonInfo> infos = new ArrayList<>();
        int adults = 0;
        int children = 0;

        for (Person p : persons) {
            int age = getAgeByName(p.getFirstName(), p.getLastName());
            if (age < 18) children++;
            else adults++;

            FirestationResponse.PersonInfo info = new FirestationResponse.PersonInfo();
            info.setFirstName(p.getFirstName());
            info.setLastName(p.getLastName());
            info.setAddress(p.getAddress());
            info.setPhone(p.getPhone());

            infos.add(info);
        }

        FirestationResponse response = new FirestationResponse();
        response.setPersons(infos);
        response.setNumberOfAdults(adults);
        response.setNumberOfChildren(children);

        return response;
    }

    public ChildAlertResponse getChildrenByAddress(String address) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        List<Person> residents = data.getPersons().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .collect(Collectors.toList());

        List<ChildAlertResponse.Child> children = new ArrayList<>();
        List<ChildAlertResponse.HouseholdMember> household = new ArrayList<>();

        for (Person p : residents) {
            int age = getAgeByName(p.getFirstName(), p.getLastName());

            if (age < 18) {
                ChildAlertResponse.Child child = new ChildAlertResponse.Child();
                child.setFirstName(p.getFirstName());
                child.setLastName(p.getLastName());
                child.setAge(age);
                children.add(child);
            } else {
                ChildAlertResponse.HouseholdMember member = new ChildAlertResponse.HouseholdMember();
                member.setFirstName(p.getFirstName());
                member.setLastName(p.getLastName());
                household.add(member);
            }
        }

        ChildAlertResponse response = new ChildAlertResponse();
        response.setChildren(children);
        response.setHouseholdMembers(household);
        return response;
    }

    public List<String> getPhoneNumbersByStation(int stationNumber) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        List<String> addresses = data.getFirestations().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        return data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());
    }

    public FireAddressResponse getResidentsByAddress(String address) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        Optional<Firestation> firestation = data.getFirestations().stream()
                .filter(f -> f.getAddress().equalsIgnoreCase(address))
                .findFirst();

        int stationNumber = firestation.map(Firestation::getStation).orElse(0);

        List<Person> persons = data.getPersons().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .collect(Collectors.toList());

        List<FireAddressResponse.Resident> residents = new ArrayList<>();

        for (Person p : persons) {
            Optional<MedicalRecord> record = data.getMedicalrecords().stream()
                    .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName()) &&
                            m.getLastName().equalsIgnoreCase(p.getLastName()))
                    .findFirst();

            FireAddressResponse.Resident r = new FireAddressResponse.Resident();
            r.setFirstName(p.getFirstName());
            r.setLastName(p.getLastName());
            r.setPhone(p.getPhone());
            r.setAge(getAgeByName(p.getFirstName(), p.getLastName()));

            record.ifPresent(med -> {
                r.setMedications(med.getMedications());
                r.setAllergies(med.getAllergies());
            });

            residents.add(r);
        }

        FireAddressResponse response = new FireAddressResponse();
        response.setStationNumber(stationNumber);
        response.setResidents(residents);
        return response;
    }

    public FloodResponse getFloodData(List<Integer> stationNumbers) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        List<String> addresses = data.getFirestations().stream()
                .filter(f -> stationNumbers.contains(f.getStation()))
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<FloodResponse.HouseholdMember>> result = new HashMap<>();

        for (String address : addresses) {
            List<FloodResponse.HouseholdMember> members = data.getPersons().stream()
                    .filter(p -> p.getAddress().equalsIgnoreCase(address))
                    .map(p -> {
                        FloodResponse.HouseholdMember member = new FloodResponse.HouseholdMember();
                        member.setFirstName(p.getFirstName());
                        member.setLastName(p.getLastName());
                        member.setPhone(p.getPhone());
                        try {
                            member.setAge(getAgeByName(p.getFirstName(), p.getLastName()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        data.getMedicalrecords().stream()
                                .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName()) &&
                                        m.getLastName().equalsIgnoreCase(p.getLastName()))
                                .findFirst()
                                .ifPresent(med -> {
                                    member.setMedications(med.getMedications());
                                    member.setAllergies(med.getAllergies());
                                });
                        return member;
                    })
                    .collect(Collectors.toList());

            result.put(address, members);
        }

        FloodResponse response = new FloodResponse();
        response.setHouseholds(result);
        return response;
    }

    public List<PersonInfoResponse> getPersonInfo(String lastName) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        return data.getPersons().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .map(p -> {
                    PersonInfoResponse info = new PersonInfoResponse();
                    info.setFirstName(p.getFirstName());
                    info.setLastName(p.getLastName());
                    info.setAddress(p.getAddress());
                    info.setEmail(p.getEmail());

                    try {
                        info.setAge(getAgeByName(p.getFirstName(), p.getLastName()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    data.getMedicalrecords().stream()
                            .filter(m -> m.getFirstName().equalsIgnoreCase(p.getFirstName()) &&
                                    m.getLastName().equalsIgnoreCase(p.getLastName()))
                            .findFirst()
                            .ifPresent(med -> {
                                info.setMedications(med.getMedications());
                                info.setAllergies(med.getAllergies());
                            });

                    return info;
                })
                .collect(Collectors.toList());
    }

    public List<String> getEmailsByCity(String city) throws IOException {
        DataWrapper data = dataLoaderService.loadData();

        return data.getPersons().stream()
                .filter(p -> p.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .distinct()
                .collect(Collectors.toList());
    }
}
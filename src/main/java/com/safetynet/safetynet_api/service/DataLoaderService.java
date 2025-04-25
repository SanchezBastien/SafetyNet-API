package com.safetynet.safetynet_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_api.model.*;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Service chargé de lire et traiter les données du fichier JSON.
 * Fournit des méthodes utilitaires pour filtrer, transformer et analyser les données
 * liées aux personnes, casernes et dossiers médicaux.
 */
@Service
public class DataLoaderService {

    /**
     * Objet Jackson utilisé pour convertir le JSON en objets Java.
     */
    private final ObjectMapper objectMapper;

    /**
     * Référence au fichier JSON à charger, injectée depuis les ressources du classpath.
     */
    @Value("classpath:data.json")
    private Resource dataFile;

/**
 * Constructeur injectant l'objet ObjectMapper utilisé pour la désérialisation JSON.
 * Parametre objectMapper (outil de mapping JSON) en objet Java*/
    public DataLoaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**Charge les données depuis le fichier JSON et les parses en l'objet DataWrapper
    *Sert de point d'entrée pour accéder aux données du fichier
    *return un objet contenant toutes les données du fichier*/
    public DataWrapper loadData() throws IOException {
        return objectMapper.readValue(dataFile.getInputStream(), DataWrapper.class);
    }

    /**Recupere la liste des personnes résidant dans une zone desservie par une station donnée*/
    public List<Person> getPersonsByStationNumber(int stationNumber) throws IOException {
        DataWrapper data = loadData(); //appel la méthode loadData pour lire et parser les données JSON
        // Récupère les adresses associées à la station
        List<String> addresses = data.getFirestations().stream() //stream démarre un flux pour traiter la liste
                .filter(f -> f.getStation() == stationNumber) //ne garde que les casernes dont le numéro correspond à stationNumber
                .map(Firestation::getAddress) //extrait l'adresse de la communauté chaque caserne concernée
                .collect(Collectors.toList()); //transforme le flux en liste
            //a ce stade, nous avons toutes les adresses couvertes par la caserne n°X
        // Filtre les personnes vivant à ces adresses
        return data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                //garde seulement celles dont l'adresse est présente dans la liste adresses
                .collect(Collectors.toList());
                //retourne le résultat sous forme de List<Person>
    }

    //Calcule l'age d'une personne (via nom/prenom) en se basant sur sa date de naissance
    public int getAgeByName(String firstName, String lastName) throws IOException {
        DataWrapper data = loadData();
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
        return 0; //si l'âge n'est pas trouvé
    }

    /** Rassemble les informations nécessaire pour répondre à une requete liée à une station :
     * personnes couverte, nombre d'adultes et d'enfants parmi eux */
    public FirestationResponse getFirestationResponse(int stationNumber) throws IOException {
        DataWrapper data = loadData();

        // Étape 1 : Récupérer les adresses desservies par cette station
        List<String> addresses = data.getFirestations().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        // Étape 2 : Filtrer les personnes vivant à ces adresses
        List<Person> persons = data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());

        // Étape 3 : Structurer la réponse avec le comptage enfants/adultes
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
        DataWrapper data = loadData();

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
        DataWrapper data = loadData();

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
        DataWrapper data = loadData();

        // Trouver le numéro de la station
        Optional<Firestation> firestation = data.getFirestations().stream()
                .filter(f -> f.getAddress().equalsIgnoreCase(address))
                .findFirst();

        int stationNumber = firestation.map(Firestation::getStation).orElse(0);

        // Filtrer les résidents à cette adresse
        List<Person> persons = data.getPersons().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .collect(Collectors.toList());

        // Préparer la liste des résidents avec leurs données médicales
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
        DataWrapper data = loadData();

        // 1. Trouver toutes les adresses desservies par les stations
        List<String> addresses = data.getFirestations().stream()
                .filter(f -> stationNumbers.contains(f.getStation()))
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());

        // 2. Préparer la map adresse => liste de personnes
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
        DataWrapper data = loadData();

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
        DataWrapper data = loadData();

        return data.getPersons().stream()
                .filter(p -> p.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .distinct()
                .collect(Collectors.toList());
    }

    private void saveData(DataWrapper data) throws IOException {
        objectMapper.writeValue(dataFile.getFile(), data);
    }

    public Person addPerson(Person person) throws IOException {
        DataWrapper data = loadData();
        data.getPersons().add(person);
        saveData(data);
        return person;
    }

    public Person updatePerson(String firstName, String lastName, Person updatedPerson) throws IOException {
        DataWrapper data = loadData();
        for (int i = 0; i < data.getPersons().size(); i++) {
            Person p = data.getPersons().get(i);
            if (p.getFirstName().equalsIgnoreCase(firstName) && p.getLastName().equalsIgnoreCase(lastName)) {
                data.getPersons().set(i, updatedPerson);
                saveData(data);
                return updatedPerson;
            }
        }
        return null;
    }

    public boolean deletePerson(String firstName, String lastName) throws IOException {
        DataWrapper data = loadData();
        boolean removed = data.getPersons().removeIf(p ->
                p.getFirstName().equalsIgnoreCase(firstName) &&
                        p.getLastName().equalsIgnoreCase(lastName));
        if (removed) saveData(data);
        return removed;
    }
}
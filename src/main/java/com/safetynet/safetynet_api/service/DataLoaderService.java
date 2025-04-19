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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//Service de lecture du JSON
@Service
public class DataLoaderService {

    private final ObjectMapper objectMapper;

    @Value("classpath:data.json")
    private Resource dataFile;

    public DataLoaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //Charge les données depuis le fichier JSON et les parses en l'objet DataWrapper
    //Sert de point d'entrée pour accéder aux données du fichier
    public DataWrapper loadData() throws IOException {
        return objectMapper.readValue(dataFile.getInputStream(), DataWrapper.class);
    }

    //Liste les résidents couverts par une caserne spécifique
    public List<Person> getPersonsByStationNumber(int stationNumber) throws IOException {
        DataWrapper data = loadData();
        List<String> addresses = data.getFirestations().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        return data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());
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
        return 0;
    }

    // Renvoie la réponse structurée : personnes desservie par une station de pompier
    // + Le nombre d'adultes et d'enfants parmi eux
    public FirestationResponse getFirestationResponse(int stationNumber) throws IOException {
        DataWrapper data = loadData();

        // Étape 1 : Récupérer les adresses de la station
        List<String> addresses = data.getFirestations().stream()
                .filter(f -> f.getStation() == stationNumber)
                .map(Firestation::getAddress)
                .collect(Collectors.toList());

        // Étape 2 : Filtrer les personnes vivant à ces adresses
        List<Person> persons = data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());

        // Étape 3 : Préparer la réponse
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
}
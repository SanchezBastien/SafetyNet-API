package com.safetynet.safetynet_api.service;

import com.safetynet.safetynet_api.exception.DataLoadingException;
import com.safetynet.safetynet_api.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de traitement des différentes alertes basées sur les données du fichier JSON.
 * Fournit des méthodes pour :
 * - Obtenir les résidents par caserne ou adresse
 * - Extraire les numéros de téléphone ou emails
 * - Regrouper les foyers pour une alerte inondation
 * - Calculer l'âge des individus
 * Ce service centralise les logiques complexes de filtrage et de transformation de données.
 */
@Service
public class AlertService {

    private final DataLoaderService dataLoaderService;
/**
 * Constructeur injectant le service de chargement de données JSON.
 * @param dataLoaderService Service d'accès aux données du fichier.
 */
 public AlertService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    // ====== Méthodes publiques (API exposée) ======
    /**
     * Récupère les personnes desservies par une station de pompiers donnée,
     * ainsi que le nombre d'adultes et d'enfants parmi eux.
     * @param stationNumber Numéro de la station de pompiers
     * @return Réponse structurée avec informations des personnes et comptage adultes/enfants
     */
    public FirestationResponse getFirestationResponse(int stationNumber) {
        DataWrapper data = loadData();
        List<String> addresses = getAddressesByStations(Collections.singletonList(stationNumber), data);

        List<FirestationResponse.PersonInfo> personsInfo = data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(p -> new FirestationResponse.PersonInfo(
                        p.getFirstName(), p.getLastName(), p.getAddress(), p.getPhone()))
                .collect(Collectors.toList());

        long children = data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(p -> computeAge(findBirthdate(p, data.getMedicalrecords())))
                .filter(age -> age < 18)
                .count();

        int total = personsInfo.size();
        int adults = (int)(total - children);

        return new FirestationResponse(personsInfo, adults, (int) children);
    }


    /**
     * Récupère les enfants habitant à une adresse donnée ainsi que les membres du foyer.
     * @param address Adresse à rechercher
     * @return Réponse contenant la liste des enfants et des membres du foyer
     */
    public ChildAlertResponse getChildrenByAddress(String address) {
        DataWrapper data = loadData();

        List<Person> personsAtAddress = data.getPersons().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .collect(Collectors.toList());

        List<ChildAlertResponse.Child> children = personsAtAddress.stream()
                .filter(p -> computeAge(findBirthdate(p, data.getMedicalrecords())) < 18)
                .map(p -> new ChildAlertResponse.Child(
                        p.getFirstName(), p.getLastName(),
                        computeAge(findBirthdate(p, data.getMedicalrecords()))))
                .collect(Collectors.toList());

        List<ChildAlertResponse.HouseholdMember> household = personsAtAddress.stream()
                .filter(p -> computeAge(findBirthdate(p, data.getMedicalrecords())) >= 18)
                .map(p -> new ChildAlertResponse.HouseholdMember(
                        p.getFirstName(), p.getLastName()))
                .collect(Collectors.toList());

        return new ChildAlertResponse(children, household);
    }

    /**
     * Récupère tous les numéros de téléphone des personnes couvertes par une station donnée.
     * @param stationNumber Numéro de la station
     * @return Liste des numéros de téléphone uniques
     */
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        DataWrapper data = loadData();
        List<String> addresses = getAddressesByStations(Collections.singletonList(stationNumber), data);

        return data.getPersons().stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Récupère les résidents d'une adresse donnée ainsi que leur numéro de station de pompiers.
     * @param address Adresse recherchée
     * @return Réponse contenant la liste des résidents et le numéro de station associé
     */
    public FireAddressResponse getResidentsByAddress(String address) {
        DataWrapper data = loadData();

        Optional<Firestation> firestation = data.getFirestations().stream()
                .filter(f -> f.getAddress().equalsIgnoreCase(address))
                .findFirst();

        int stationNumber = firestation.map(Firestation::getStation).orElse(0);

        List<FireAddressResponse.Resident> residents = data.getPersons().stream()
                .filter(p -> p.getAddress().equalsIgnoreCase(address))
                .map(p -> buildResident(p, data.getMedicalrecords()))
                .collect(Collectors.toList());

        return new FireAddressResponse(stationNumber, residents);
    }

    /**
     * Regroupe les habitants par adresse pour toutes les stations spécifiées (alerte inondation).
     * @param stationNumbers Liste des numéros de stations
     * @return Réponse structurée regroupant les foyers par adresse
     */
    public FloodResponse getFloodData(List<Integer> stationNumbers) {
        DataWrapper data = loadData();
        List<String> addresses = getAddressesByStations(stationNumbers, data);

        Map<String, List<FloodResponse.HouseholdMember>> households = new HashMap<>();

        for (String address : addresses) {
            List<FloodResponse.HouseholdMember> members = data.getPersons().stream()
                    .filter(p -> p.getAddress().equalsIgnoreCase(address))
                    .map(p -> buildHouseholdMember(p, data.getMedicalrecords()))
                    .collect(Collectors.toList());

            households.put(address, members);
        }

        return new FloodResponse(households);
    }

    /**
     * Récupère les informations détaillées d'une personne par son nom de famille.
     * @param lastName Nom de famille recherché
     * @return Liste des informations de personnes correspondantes
     */
    public List<PersonInfoResponse> getPersonInfo(String lastName) {
        DataWrapper data = loadData();

        return data.getPersons().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .map(p -> buildPersonInfo(p, data.getMedicalrecords()))
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les emails des personnes habitant dans une ville donnée.
     * @param city Nom de la ville
     * @return Liste des adresses email
     * @throws IOException en cas d'erreur de lecture des données
     */
    public List<String> getEmailsByCity(String city) throws IOException {
        DataWrapper data = loadData();

        return data.getPersons().stream()
                .filter(p -> p.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .distinct()
                .collect(Collectors.toList());
    }

    // ====== Méthodes privées utilitaires ======

    /**
     * Charge les données du fichier JSON via le service DataLoaderService.
     * @return L'ensemble des données chargées sous forme de DataWrapper
     */
    private DataWrapper loadData() {
        try {
            return dataLoaderService.loadData();
        } catch (IOException e) {
            throw new DataLoadingException("Erreur lors du chargement des données", e);
        }
    }

    /**
     * Récupère les adresses associées aux numéros de stations spécifiés.
     * @param stationNumbers Liste des numéros de stations
     * @param data Données chargées depuis le fichier JSON
     * @return Liste d'adresses couvertes par les stations données
     */
    private List<String> getAddressesByStations(List<Integer> stationNumbers, DataWrapper data) {
        return data.getFirestations().stream()
                .filter(f -> stationNumbers.contains(f.getStation()))
                .map(Firestation::getAddress)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Cherche la date de naissance d'une personne en fonction de son prénom et nom
     * @param person Personne pour laquelle trouver la date de naissance
     * @param records Liste des dossiers médicaux
     * @return Date de naissance au format MM/dd/yyyy, ou "01/01/1970" si non trouvée
     */
    private String findBirthdate(Person person, List<MedicalRecord> records) {
        return records.stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                        m.getLastName().equalsIgnoreCase(person.getLastName()))
                .map(MedicalRecord::getBirthdate)
                .findFirst()
                .orElse("01/01/1970"); // Valeur par défaut raisonnable
    }

    /**
     * Calcule l'âge d'une personne à partir de sa date de naissance.
     * @param birthdate Date de naissance au format MM/dd/yyyy
     * @return Âge actuel en années
     */
    private int computeAge(String birthdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birth = LocalDate.parse(birthdate, formatter);
        return Period.between(birth, LocalDate.now()).getYears();
    }

    /**
     * Construit un objet Resident enrichi pour un résident donné.
     * @param person La personne concernée
     * @param records Liste des dossiers médicaux
     * @return Résident enrichi avec téléphone, âge, médicaments et allergies
     */
    private FireAddressResponse.Resident buildResident(Person person, List<MedicalRecord> records) {
        FireAddressResponse.Resident resident = new FireAddressResponse.Resident();
        resident.setFirstName(person.getFirstName());
        resident.setLastName(person.getLastName());
        resident.setPhone(person.getPhone());
        resident.setAge(computeAge(findBirthdate(person, records)));

        records.stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                        m.getLastName().equalsIgnoreCase(person.getLastName()))
                .findFirst()
                .ifPresent(m -> {
                    resident.setMedications(m.getMedications());
                    resident.setAllergies(m.getAllergies());
                });

        return resident;
    }

    /**
     * Construit un objet HouseholdMember enrichi pour une inondation (flood alert).
     * @param person La personne concernée
     * @param records Liste des dossiers médicaux
     * @return Membre du foyer enrichi avec téléphone, âge, médicaments et allergies
     */
    private FloodResponse.HouseholdMember buildHouseholdMember(Person person, List<MedicalRecord> records) {
        FloodResponse.HouseholdMember member = new FloodResponse.HouseholdMember();
        member.setFirstName(person.getFirstName());
        member.setLastName(person.getLastName());
        member.setPhone(person.getPhone());
        member.setAge(computeAge(findBirthdate(person, records)));

        records.stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                        m.getLastName().equalsIgnoreCase(person.getLastName()))
                .findFirst()
                .ifPresent(m -> {
                    member.setMedications(m.getMedications());
                    member.setAllergies(m.getAllergies());
                });

        return member;
    }

    /**
     * Construit un objet PersonInfoResponse enrichi pour une personne donnée.
     * @param person La personne concernée
     * @param records Liste des dossiers médicaux
     * @return Informations complètes de la personne (adresse, âge, email, médicaments, allergies)
     */
    private PersonInfoResponse buildPersonInfo(Person person, List<MedicalRecord> records) {
        PersonInfoResponse info = new PersonInfoResponse();
        info.setFirstName(person.getFirstName());
        info.setLastName(person.getLastName());
        info.setAddress(person.getAddress());
        info.setEmail(person.getEmail());
        info.setAge(computeAge(findBirthdate(person, records)));

        records.stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                        m.getLastName().equalsIgnoreCase(person.getLastName()))
                .findFirst()
                .ifPresent(m -> {
                    info.setMedications(m.getMedications());
                    info.setAllergies(m.getAllergies());
                });

        return info;
    }
}
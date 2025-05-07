package com.safetynet.safetynet_api.service;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.model.Person;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PersonService {

    private final DataLoaderService dataLoaderService;

    public PersonService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public Person addPerson(Person person) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        data.getPersons().add(person);
        dataLoaderService.saveData(data);
        return person;
    }

    public Person updatePerson(String firstName, String lastName, Person updatedPerson) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        for (int i = 0; i < data.getPersons().size(); i++) {
            Person p = data.getPersons().get(i);
            if (p.getFirstName().equalsIgnoreCase(firstName) && p.getLastName().equalsIgnoreCase(lastName)) {
                data.getPersons().set(i, updatedPerson);
                dataLoaderService.saveData(data);
                return updatedPerson;
            }
        }
        return null;
    }

    public boolean deletePerson(String firstName, String lastName) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        boolean removed = data.getPersons().removeIf(p ->
                p.getFirstName().equalsIgnoreCase(firstName) &&
                        p.getLastName().equalsIgnoreCase(lastName)
        );
        if (removed) {
            dataLoaderService.saveData(data);
        }
        return removed;
    }

    public Object getPersonsByAddress(String s) {
        return null;
    }
}
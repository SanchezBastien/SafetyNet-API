package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.model.Person;
import com.safetynet.safetynet_api.service.DataLoaderService;
import com.safetynet.safetynet_api.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    private DataLoaderService dataLoaderService;
    private PersonService personService;
    private DataWrapper dataWrapper;

    @BeforeEach
    void setUp() {
        dataLoaderService = mock(DataLoaderService.class);
        personService = new PersonService(dataLoaderService);
        dataWrapper = new DataWrapper();
        dataWrapper.setPersons(new ArrayList<>());
    }

    @Test
    void addPerson_addsToList() throws IOException {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");

        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        Person result = personService.addPerson(person);

        assertEquals("John", result.getFirstName());
        verify(dataLoaderService).saveData(dataWrapper);
        assertTrue(dataWrapper.getPersons().contains(person));
    }

    @Test
    void updatePerson_updatesMatchingPerson() throws IOException {
        Person original = new Person();
        original.setFirstName("Jane");
        original.setLastName("Smith");

        dataWrapper.getPersons().add(original);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        Person updated = new Person();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setAddress("123 Updated Street");

        Person result = personService.updatePerson("Jane", "Smith", updated);

        assertEquals("123 Updated Street", result.getAddress());
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void deletePerson_removesIfExists() throws IOException {
        Person person = new Person();
        person.setFirstName("Alice");
        person.setLastName("Johnson");

        dataWrapper.getPersons().add(person);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        boolean result = personService.deletePerson("Alice", "Johnson");

        assertTrue(result);
        verify(dataLoaderService).saveData(dataWrapper);
        assertFalse(dataWrapper.getPersons().contains(person));
    }
}

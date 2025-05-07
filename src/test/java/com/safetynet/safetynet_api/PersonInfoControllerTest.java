package com.safetynet.safetynet_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_api.controller.PersonInfoController;
import com.safetynet.safetynet_api.model.Person;
import com.safetynet.safetynet_api.model.PersonInfoResponse;
import com.safetynet.safetynet_api.service.AlertService;
import com.safetynet.safetynet_api.service.PersonService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonInfoController.class)
@Import(PersonInfoControllerTest.MockConfig.class)
class PersonInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;

    @Autowired
    private AlertService alertService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public PersonService personService() {
            return Mockito.mock(PersonService.class);
        }

        @Bean
        public AlertService alertService() {
            return Mockito.mock(AlertService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
    /*Requête GET avec filtre sur le lastName
    /*Création d’un nouvel objet Person via POST
    /*Mise à jour et suppression par nom/prénom
    /*Structure du JSON attendu*/
    @Test
    void getInfo_whenLastNameValid_shouldReturnList() throws Exception {
        PersonInfoResponse personInfo = new PersonInfoResponse(
                "Jane", "Doe", "123 Main St", 25, "jane@example.com",
                List.of("med1"), List.of("allergy1")
        );
        when(alertService.getPersonInfo("Doe")).thenReturn(List.of(personInfo));

        mockMvc.perform(get("/personInfo").param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jane"))
                .andExpect(jsonPath("$[0].email").value("jane@example.com"));
    }

    @Test
    void addPerson_shouldReturnCreatedPerson() throws Exception {
        Person person = new Person();
        person.setFirstName("Tom");
        person.setLastName("Hardy");
        person.setAddress("456 Elm St");
        person.setCity("Culver");
        person.setZip("97451");
        person.setPhone("8418746512");
        person.setEmail("tom@example.com");

        when(personService.addPerson(Mockito.any(Person.class))).thenReturn(person);

        mockMvc.perform(post("/personInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(person)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Tom"))
                .andExpect(jsonPath("$.email").value("tom@example.com"));
    }

    @Test
    void updatePerson_shouldReturnUpdatedPerson() throws Exception {
        Person updated = new Person();
        updated.setFirstName("Tom");
        updated.setLastName("Hardy");
        updated.setAddress("789 Pine St");
        updated.setCity("Culver");
        updated.setZip("97451");
        updated.setPhone("8418741234");
        updated.setEmail("tom.hardy@example.com");

        when(personService.updatePerson("Tom", "Hardy", updated)).thenReturn(updated);

        mockMvc.perform(put("/personInfo")
                        .param("firstName", "Tom")
                        .param("lastName", "Hardy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("789 Pine St"))
                .andExpect(jsonPath("$.email").value("tom.hardy@example.com"));
    }

    @Test
    void deletePerson_shouldReturnTrue() throws Exception {
        when(personService.deletePerson("Tom", "Hardy")).thenReturn(true);

        mockMvc.perform(delete("/personInfo")
                        .param("firstName", "Tom")
                        .param("lastName", "Hardy"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
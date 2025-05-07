package com.safetynet.safetynet_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_api.controller.FirestationManagementController;
import com.safetynet.safetynet_api.model.Firestation;
import com.safetynet.safetynet_api.service.FirestationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FirestationManagementController.class)
@Import(FirestationManagementControllerTest.MockConfig.class)
class FirestationManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FirestationService firestationService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public FirestationService firestationService() {
            return Mockito.mock(FirestationService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
    /* Tous les types de requêtes (CRUD)
    /* Utilisation correcte de ObjectMapper pour POST/PUT
    /* Paramètres de requête et corps JSON */
    @Test
    void getAllFirestations_shouldReturnList() throws Exception {
        when(firestationService.getAllFirestations()).thenReturn(Collections.singletonList(new Firestation("1509 Culver St", 3)));

        mockMvc.perform(get("/firestation-management"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$[0].station").value(3));
    }

    @Test
    void addFirestation_shouldReturnCreatedFirestation() throws Exception {
        Firestation input = new Firestation("29 15th St", 2);
        when(firestationService.addFirestation(Mockito.any(Firestation.class))).thenReturn(input);

        mockMvc.perform(post("/firestation-management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("29 15th St"))
                .andExpect(jsonPath("$.station").value(2));
    }

    @Test
    void updateFirestation_shouldReturnUpdatedFirestation() throws Exception {
        Firestation updated = new Firestation("29 15th St", 4);
        when(firestationService.updateFirestation("29 15th St", 4)).thenReturn(updated);

        mockMvc.perform(put("/firestation-management")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("29 15th St"))
                .andExpect(jsonPath("$.station").value(4));
    }

    @Test
    void deleteFirestation_shouldReturnTrue() throws Exception {
        when(firestationService.deleteFirestation("29 15th St")).thenReturn(true);

        mockMvc.perform(delete("/firestation-management")
                        .param("address", "29 15th St"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
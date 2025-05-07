package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.FloodController;
import com.safetynet.safetynet_api.model.FloodResponse;
import com.safetynet.safetynet_api.model.FloodResponse.HouseholdMember;
import com.safetynet.safetynet_api.service.AlertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FloodController.class)
@Import(FloodControllerTest.MockConfig.class)
class FloodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertService alertService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AlertService alertService() {
            return Mockito.mock(AlertService.class);
        }
    }
    /*Requête GET avec plusieurs paramètres stations
    /*Structure et contenu de la réponse JSON
    /*Mapping par adresse (clé du Map) et valeurs (liste d'habitants)*/
    @Test
    void getFloodInfo_whenStationsAreValid_thenReturnHouseholds() throws Exception {
        // Préparation des données mockées
        Map<String, List<HouseholdMember>> households = new HashMap<>();
        households.put("1509 Culver St", List.of(
                new HouseholdMember("John", "Doe", "841-874-6512", 36, List.of("med1"), List.of("allergy1"))
        ));

        when(alertService.getFloodData(List.of(1, 2))).thenReturn(new FloodResponse(households));

        // Appel de l'endpoint avec stations multiples
        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.households['1509 Culver St']").isArray())
                .andExpect(jsonPath("$.households['1509 Culver St'][0].firstName").value("John"))
                .andExpect(jsonPath("$.households['1509 Culver St'][0].age").value(36));
    }
}

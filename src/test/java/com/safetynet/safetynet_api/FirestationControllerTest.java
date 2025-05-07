package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.FirestationController;
import com.safetynet.safetynet_api.model.FirestationResponse;
import com.safetynet.safetynet_api.model.FirestationResponse.PersonInfo;
import com.safetynet.safetynet_api.service.AlertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FirestationController.class)
@Import(FirestationControllerTest.MockConfig.class)
class FirestationControllerTest {

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
    //*Le traitement d’un paramètre stationNumber
    /* Le format et contenu du JSON retourné
    /* Une réponse avec une personne et le bon comptage adultes/enfants */

    @Test
    void getPersonsByStation_whenStationExists_thenReturnPeopleList() throws Exception {
        FirestationResponse response = new FirestationResponse(
                Collections.singletonList(new PersonInfo("John", "Doe", "1509 Culver St", "841-874-6512")),
                1, 0
        );

        when(alertService.getFirestationResponse(3)).thenReturn(response);

        mockMvc.perform(get("/firestation").param("stationNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons").isArray())
                .andExpect(jsonPath("$.persons[0].firstName").value("John"))
                .andExpect(jsonPath("$.numberOfAdults").value(1))
                .andExpect(jsonPath("$.numberOfChildren").value(0));
    }
}
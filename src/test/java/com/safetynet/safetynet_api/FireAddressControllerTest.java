package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.FireAddressController;
import com.safetynet.safetynet_api.model.FireAddressResponse;
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

@WebMvcTest(FireAddressController.class)
@Import(FireAddressControllerTest.MockConfig.class)
class FireAddressControllerTest {

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

    /*L’appel du contrôleur avec un paramètre
    /*Le comportement attendu en réponse HTTP 200
    /*Le JSON retourné (structure minimale vérifiée)*/
    @Test
    void getFireData_whenAddressIsValid_thenReturnResidents() throws Exception {
        FireAddressResponse mockResponse = new FireAddressResponse(3, Collections.emptyList());
        when(alertService.getResidentsByAddress("1509 Culver St")).thenReturn(mockResponse);

        mockMvc.perform(get("/fire").param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationNumber").value(3))
                .andExpect(jsonPath("$.residents").isArray());
    }
}

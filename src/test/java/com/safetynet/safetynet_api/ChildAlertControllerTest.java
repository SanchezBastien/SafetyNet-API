package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.ChildAlertController;
import com.safetynet.safetynet_api.model.ChildAlertResponse;
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

@WebMvcTest(ChildAlertController.class)
@Import(ChildAlertControllerTest.TestConfig.class)
class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertService alertService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AlertService alertService() {
            return Mockito.mock(AlertService.class);
        }
    }


    /*Appel du contrôleur
    /*Passage d'un paramètre address
    /*Retour d'une réponse correcte (code 200)
    /*Structure du JSON*/
    @Test
    void getChildren_whenValidAddress_thenReturnsEmptyLists() throws Exception {
        // Mock data
        ChildAlertResponse response = new ChildAlertResponse(Collections.emptyList(), Collections.emptyList());
        when(alertService.getChildrenByAddress("1509 Culver St")).thenReturn(response);

        // Perform request
        mockMvc.perform(get("/childAlert")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.householdMembers").isArray());
    }
}
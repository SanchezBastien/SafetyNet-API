package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.CommunityEmailController;
import com.safetynet.safetynet_api.service.AlertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommunityEmailController.class)
@Import(CommunityEmailControllerTest.MockConfig.class)
class CommunityEmailControllerTest {

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

    /*L’appel au bon endpoint avec un paramètre city
    Le retour d’une liste d’emails
    Le format JSON attendu*/
    @Test
    void getEmails_whenCityIsValid_thenReturnEmailsList() throws Exception {
        List<String> emails = Arrays.asList("john@example.com", "jane@example.com");
        Mockito.when(alertService.getEmailsByCity("Culver")).thenReturn(emails);

        mockMvc.perform(get("/communityEmail").param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("john@example.com"))
                .andExpect(jsonPath("$[1]").value("jane@example.com"));
    }
}


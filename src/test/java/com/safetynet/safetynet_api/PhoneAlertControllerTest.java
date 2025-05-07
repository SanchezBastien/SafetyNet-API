package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.PhoneAlertController;
import com.safetynet.safetynet_api.service.AlertService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhoneAlertController.class)
@Import(PhoneAlertControllerTest.MockConfig.class)
class PhoneAlertControllerTest {

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

    /*Le passage correct du paramètre firestation
    /*Le retour d'une liste JSON de numéros
    /*L'intégration correcte avec le service mocké*/
    @Test
    void getPhones_whenFirestationIsValid_thenReturnPhoneList() throws Exception {
        List<String> phones = List.of("841-874-6512", "841-874-8547");
        when(alertService.getPhoneNumbersByStation(3)).thenReturn(phones);

        mockMvc.perform(get("/phoneAlert").param("firestation", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("841-874-6512"))
                .andExpect(jsonPath("$[1]").value("841-874-8547"));
    }
}

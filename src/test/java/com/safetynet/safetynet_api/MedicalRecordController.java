package com.safetynet.safetynet_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_api.controller.MedicalRecordController;
import com.safetynet.safetynet_api.model.MedicalRecord;
import com.safetynet.safetynet_api.service.MedicalRecordService;
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

@WebMvcTest(MedicalRecordController.class)
@Import(MedicalRecordControllerTest.MockConfig.class)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public MedicalRecordService medicalRecordService() {
            return Mockito.mock(MedicalRecordService.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    /*Lecture (GET), ajout (POST), mise à jour (PUT), suppression (DELETE)
    /*Passage de paramètres (@RequestParam) et corps JSON (@RequestBody)
    /*Comportement attendu avec une réponse simulée*/
    @Test
    void getAllMedicalRecords_shouldReturnList() throws Exception {
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("Jane");
        record.setLastName("Doe");
        record.setBirthdate("01/01/2000");
        record.setMedications(List.of("med1"));
        record.setAllergies(List.of("allergy1"));

        when(medicalRecordService.getAllMedicalRecords()).thenReturn(List.of(record));

        mockMvc.perform(get("/medicalRecord"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @Test
    void addMedicalRecord_shouldReturnCreatedRecord() throws Exception {
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("Tom");
        record.setLastName("Hardy");
        record.setBirthdate("02/02/1990");
        record.setMedications(List.of("medX"));
        record.setAllergies(List.of("none"));

        when(medicalRecordService.addMedicalRecord(Mockito.any(MedicalRecord.class))).thenReturn(record);

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Tom"));
    }

    @Test
    void updateMedicalRecord_shouldReturnUpdatedRecord() throws Exception {
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("Tom");
        record.setLastName("Hardy");
        record.setBirthdate("02/02/1990");
        record.setMedications(List.of("medUpdated"));
        record.setAllergies(List.of("none"));

        when(medicalRecordService.updateMedicalRecord("Tom", "Hardy", record)).thenReturn(record);

        mockMvc.perform(put("/medicalRecord")
                        .param("firstName", "Tom")
                        .param("lastName", "Hardy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medications[0]").value("medUpdated"));
    }

    @Test
    void deleteMedicalRecord_shouldReturnTrue() throws Exception {
        when(medicalRecordService.deleteMedicalRecord("Tom", "Hardy")).thenReturn(true);

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "Tom")
                        .param("lastName", "Hardy"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
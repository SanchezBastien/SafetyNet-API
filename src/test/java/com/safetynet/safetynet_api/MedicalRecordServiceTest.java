package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.model.MedicalRecord;
import com.safetynet.safetynet_api.service.DataLoaderService;
import com.safetynet.safetynet_api.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalRecordServiceTest {

    private DataLoaderService dataLoaderService;
    private MedicalRecordService medicalRecordService;
    private DataWrapper dataWrapper;

    @BeforeEach
    void setUp() {
        dataLoaderService = mock(DataLoaderService.class);
        medicalRecordService = new MedicalRecordService(dataLoaderService);
        dataWrapper = new DataWrapper();
        dataWrapper.setMedicalrecords(new ArrayList<>());
    }

    @Test
    void addMedicalRecord_addsRecordToList() throws IOException {
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("John");
        record.setLastName("Doe");

        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        MedicalRecord result = medicalRecordService.addMedicalRecord(record);

        assertEquals("John", result.getFirstName());
        assertTrue(dataWrapper.getMedicalrecords().contains(record));
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void updateMedicalRecord_updatesIfExists() throws IOException {
        MedicalRecord original = new MedicalRecord();
        original.setFirstName("Jane");
        original.setLastName("Smith");

        dataWrapper.getMedicalrecords().add(original);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        MedicalRecord updated = new MedicalRecord();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setBirthdate("01/01/1990");

        MedicalRecord result = medicalRecordService.updateMedicalRecord("Jane", "Smith", updated);

        assertEquals("01/01/1990", result.getBirthdate());
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void deleteMedicalRecord_removesIfExists() throws IOException {
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("Alice");
        record.setLastName("Johnson");

        dataWrapper.getMedicalrecords().add(record);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        boolean result = medicalRecordService.deleteMedicalRecord("Alice", "Johnson");

        assertTrue(result);
        assertFalse(dataWrapper.getMedicalrecords().contains(record));
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void getAllMedicalRecords_returnsList() throws IOException {
        MedicalRecord m1 = new MedicalRecord();
        m1.setFirstName("Emma");
        m1.setLastName("Brown");

        List<MedicalRecord> list = List.of(m1);
        dataWrapper.setMedicalrecords(new ArrayList<>(list));

        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        List<MedicalRecord> result = medicalRecordService.getAllMedicalRecords();

        assertEquals(1, result.size());
        assertEquals("Emma", result.get(0).getFirstName());
    }
}

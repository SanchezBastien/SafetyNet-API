package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.model.Firestation;
import com.safetynet.safetynet_api.service.DataLoaderService;
import com.safetynet.safetynet_api.service.FirestationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FirestationServiceTest {

    private DataLoaderService dataLoaderService;
    private FirestationService firestationService;
    private DataWrapper dataWrapper;

    @BeforeEach
    void setUp() {
        dataLoaderService = mock(DataLoaderService.class);
        firestationService = new FirestationService(dataLoaderService);
        dataWrapper = new DataWrapper();
        dataWrapper.setFirestations(new ArrayList<>());
    }

    @Test
    void addFirestation_addsToList() throws IOException {
        Firestation firestation = new Firestation();
        firestation.setAddress("123 Fire St");
        firestation.setStation(1);

        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        Firestation result = firestationService.addFirestation(firestation);

        assertTrue(dataWrapper.getFirestations().contains(firestation));
        assertEquals(1, result.getStation());
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void updateFirestation_updatesStationNumber() throws IOException {
        Firestation firestation = new Firestation();
        firestation.setAddress("456 Maple Ave");
        firestation.setStation(2);

        dataWrapper.getFirestations().add(firestation);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        Firestation result = firestationService.updateFirestation("456 Maple Ave", 5);

        assertNotNull(result);
        assertEquals(5, result.getStation());
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void deleteFirestation_removesIfExists() throws IOException {
        Firestation firestation = new Firestation();
        firestation.setAddress("789 Elm St");
        firestation.setStation(3);

        dataWrapper.getFirestations().add(firestation);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        boolean result = firestationService.deleteFirestation("789 Elm St");

        assertTrue(result);
        assertFalse(dataWrapper.getFirestations().contains(firestation));
        verify(dataLoaderService).saveData(dataWrapper);
    }

    @Test
    void getAllFirestations_returnsList() throws IOException {
        Firestation firestation = new Firestation();
        firestation.setAddress("321 Pine Rd");

        dataWrapper.getFirestations().add(firestation);
        when(dataLoaderService.loadData()).thenReturn(dataWrapper);

        List<Firestation> result = firestationService.getAllFirestations();

        assertEquals(1, result.size());
        assertEquals("321 Pine Rd", result.get(0).getAddress());
    }
}

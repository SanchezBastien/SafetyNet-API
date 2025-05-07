package com.safetynet.safetynet_api;

import com.safetynet.safetynet_api.controller.ChildAlertController;
import com.safetynet.safetynet_api.model.ChildAlertResponse;
import com.safetynet.safetynet_api.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ChildAlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private ChildAlertController childAlertController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChildren_ReturnsResponse() throws IOException {
        String address = "123 Main St";
        ChildAlertResponse mockResponse = new ChildAlertResponse();
        when(alertService.getChildrenByAddress(address)).thenReturn(mockResponse);

        ChildAlertResponse response = childAlertController.getChildren(address);
        assertEquals(mockResponse, response);
    }

    @Test
    void testGetChildren_ThrowsRuntimeException() {
        String address = "123 Main St";
        when(alertService.getChildrenByAddress(address)).thenThrow(new RuntimeException("Erreur simulée"));

        assertThrows(RuntimeException.class, () -> {
            childAlertController.getChildren(address);
        });
    }
}
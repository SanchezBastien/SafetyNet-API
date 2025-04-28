package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.Firestation;
import com.safetynet.safetynet_api.model.PersonInfoResponse;
import com.safetynet.safetynet_api.service.FirestationService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/firestation-management")
public class FirestationManagementController {

    private final FirestationService firestationService;

    public FirestationManagementController(FirestationService firestationService) {
        this.firestationService = firestationService;
    }

    @GetMapping
    public List<Firestation> getAllFirestations() throws IOException {
        return firestationService.getAllFirestations();
    }

    @PostMapping
    public Firestation addFirestation(@RequestBody Firestation firestation) throws IOException {
        return firestationService.addFirestation(firestation);
    }

    @PutMapping
    public Firestation updateFirestation(@RequestBody Firestation firestation) throws IOException {
        return firestationService.updateFirestation(firestation.getAddress(), firestation.getStation());
    }

    @DeleteMapping
    public boolean deleteFirestation(@RequestParam String address) throws IOException {
        return firestationService.deleteFirestation(address);
    }
}

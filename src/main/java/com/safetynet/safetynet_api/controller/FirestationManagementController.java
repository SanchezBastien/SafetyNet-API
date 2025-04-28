package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.Firestation;
import com.safetynet.safetynet_api.model.PersonInfoResponse;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/firestation-management")
public class FirestationManagementController {

    private final DataLoaderService dataService;

    public FirestationManagementController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public List<Firestation> getAllFirestations() throws IOException {
        return dataService.getAllFirestations();
    }

    @PostMapping
    public Firestation addFirestation(@RequestBody Firestation firestation) throws IOException {
        return dataService.addFirestation(firestation);
    }

    @PutMapping
    public Firestation updateFirestation(@RequestBody Firestation firestation) throws IOException {
        return dataService.updateFirestation(firestation.getAddress(), firestation.getStation());
    }

    @DeleteMapping
    public boolean deleteFirestation(@RequestParam String address) throws IOException {
        return dataService.deleteFirestation(address);
    }
}

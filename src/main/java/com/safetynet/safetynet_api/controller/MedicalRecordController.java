package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.MedicalRecord;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final DataLoaderService dataService;

    public MedicalRecordController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public List<MedicalRecord> getAllMedicalRecords() throws IOException {
        return dataService.getAllMedicalRecords();
    }

    @PostMapping
    public MedicalRecord addMedicalRecord(@RequestBody MedicalRecord record) throws IOException {
        return dataService.addMedicalRecord(record);
    }

    @PutMapping(produces = "application/json")
    public MedicalRecord updateMedicalRecord(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestBody MedicalRecord record) throws IOException {
        return dataService.updateMedicalRecord(firstName, lastName, record);
    }

    @DeleteMapping
    public boolean deleteMedicalRecord(
            @RequestParam String firstName,
            @RequestParam String lastName) throws IOException {
        return dataService.deleteMedicalRecord(firstName, lastName);
    }
}

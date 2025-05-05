package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.MedicalRecord;
import com.safetynet.safetynet_api.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public List<MedicalRecord> getAllMedicalRecords() throws IOException {
        return medicalRecordService.getAllMedicalRecords();
    }

    //pour endpoint service
    @PostMapping
    public MedicalRecord addMedicalRecord(@RequestBody @Valid MedicalRecord record) throws IOException {
        return medicalRecordService.addMedicalRecord(record);
    }

    @PutMapping(produces = "application/json")
    public MedicalRecord updateMedicalRecord(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestBody MedicalRecord record) throws IOException {
        return medicalRecordService.updateMedicalRecord(firstName, lastName, record);
    }

    @DeleteMapping
    public boolean deleteMedicalRecord(
            @RequestParam String firstName,
            @RequestParam String lastName) throws IOException {
        return medicalRecordService.deleteMedicalRecord(firstName, lastName);
    }
}

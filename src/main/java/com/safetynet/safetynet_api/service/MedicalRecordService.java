package com.safetynet.safetynet_api.service;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.model.MedicalRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MedicalRecordService {

    private final DataLoaderService dataLoaderService;

    public MedicalRecordService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public List<MedicalRecord> getAllMedicalRecords() throws IOException {
        return dataLoaderService.loadData().getMedicalrecords();
    }

    public MedicalRecord addMedicalRecord(MedicalRecord record) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        data.getMedicalrecords().add(record);
        dataLoaderService.saveData(data);
        return record;
    }

    public MedicalRecord updateMedicalRecord(String firstName, String lastName, MedicalRecord updatedRecord) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        for (int i = 0; i < data.getMedicalrecords().size(); i++) {
            MedicalRecord m = data.getMedicalrecords().get(i);
            if (m.getFirstName().equalsIgnoreCase(firstName) && m.getLastName().equalsIgnoreCase(lastName)) {
                data.getMedicalrecords().set(i, updatedRecord);
                dataLoaderService.saveData(data);
                return updatedRecord;
            }
        }
        return null;
    }

    public boolean deleteMedicalRecord(String firstName, String lastName) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        boolean removed = data.getMedicalrecords().removeIf(m ->
                m.getFirstName().equalsIgnoreCase(firstName) &&
                        m.getLastName().equalsIgnoreCase(lastName)
        );
        if (removed) {
            dataLoaderService.saveData(data);
        }
        return removed;
    }
}
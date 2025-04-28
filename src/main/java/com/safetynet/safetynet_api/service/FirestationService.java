package com.safetynet.safetynet_api.service;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.model.Firestation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FirestationService {

    private final DataLoaderService dataLoaderService;

    public FirestationService(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    public List<Firestation> getAllFirestations() throws IOException {
        return dataLoaderService.loadData().getFirestations();
    }

    public Firestation addFirestation(Firestation firestation) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        data.getFirestations().add(firestation);
        dataLoaderService.saveData(data);
        return firestation;
    }

    public Firestation updateFirestation(String address, int newStationNumber) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        for (Firestation f : data.getFirestations()) {
            if (f.getAddress().equalsIgnoreCase(address)) {
                f.setStation(newStationNumber);
                dataLoaderService.saveData(data);
                return f;
            }
        }
        return null;
    }

    public boolean deleteFirestation(String address) throws IOException {
        DataWrapper data = dataLoaderService.loadData();
        boolean removed = data.getFirestations().removeIf(f -> f.getAddress().equalsIgnoreCase(address));
        if (removed) {
            dataLoaderService.saveData(data);
        }
        return removed;
    }
}

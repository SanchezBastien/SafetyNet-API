package com.safetynet.safetynet_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.safetynet_api.model.DataWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DataLoaderService {

    private final ObjectMapper objectMapper;

    @Value("classpath:data.json")
    private Resource dataFile;

    public DataLoaderService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DataWrapper loadData() throws IOException {
        return objectMapper.readValue(dataFile.getInputStream(), DataWrapper.class);
    }

    public void saveData(DataWrapper data) throws IOException {
        objectMapper.writeValue(dataFile.getFile(), data);
    }

    public Object getPersons() {
        return null;
    }

    public Object getFirestations() {
        return null;
    }

    public Object getMedicalRecords() {
        return null;
    }

}

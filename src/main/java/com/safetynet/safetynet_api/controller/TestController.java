package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.DataWrapper;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {

    private final DataLoaderService dataLoaderService;

    public TestController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @GetMapping("/data")
    public DataWrapper getData() throws IOException {
        return dataLoaderService.loadData();
    }
}
//Test l'affichage du fichier JSON
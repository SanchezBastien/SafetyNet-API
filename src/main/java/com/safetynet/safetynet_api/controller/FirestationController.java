package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.FirestationResponse;
import com.safetynet.safetynet_api.model.Person;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Contrôleur REST pour gérer les requêtes liées aux stations de pompiers.*/
@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private final DataLoaderService dataService;

    /**Constructeur injectant le service de chargement des données.*/
    public FirestationController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    /**Endpoint HTTP */
    @GetMapping
    public FirestationResponse getPersonsByStation(@RequestParam int stationNumber) throws IOException {
        return dataService.getFirestationResponse(stationNumber);
    }
}

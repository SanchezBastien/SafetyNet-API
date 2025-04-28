package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.FirestationResponse;
import com.safetynet.safetynet_api.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


/** Contrôleur REST pour gérer les requêtes liées aux stations de pompiers.
 *Expose un endpoint pour otebnir la liste des personnes desservies par une station*/

@RestController
@RequestMapping("/firestation") //Définit le chemin de base pour tous les endpoints de cette classe
public class FirestationController {

    /**
     * Service injecté permettant de charger et manipuler les données à partir du fichier JSON.
     */
    private final AlertService alertService;

    /**Constructeur injectant le service de chargement des données.*/
    public FirestationController(AlertService alertService) {
        this.alertService = alertService;
    }

    /**Endpoint HTTP */
    @GetMapping
    public FirestationResponse getPersonsByStation(@RequestParam int stationNumber) throws IOException {
        return alertService.getFirestationResponse(stationNumber);
    }
}

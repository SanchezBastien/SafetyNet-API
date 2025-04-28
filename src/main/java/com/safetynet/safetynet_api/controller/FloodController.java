package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.FloodResponse;
import com.safetynet.safetynet_api.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/flood/stations")
public class FloodController {

    private final AlertService alertService;

    public FloodController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public FloodResponse getFloodInfo(@RequestParam List<Integer> stations) throws IOException {
        return alertService.getFloodData(stations);
    }
}

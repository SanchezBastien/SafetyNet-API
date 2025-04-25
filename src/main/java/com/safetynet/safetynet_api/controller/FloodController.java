package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.FloodResponse;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/flood/stations")
public class FloodController {

    private final DataLoaderService dataService;

    public FloodController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public FloodResponse getFloodInfo(@RequestParam List<Integer> stations) throws IOException {
        return dataService.getFloodData(stations);
    }
}

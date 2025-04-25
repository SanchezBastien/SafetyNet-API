package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private final DataLoaderService dataService;

    public CommunityEmailController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public List<String> getEmails(@RequestParam String city) throws IOException {
        return dataService.getEmailsByCity(city);
    }
}
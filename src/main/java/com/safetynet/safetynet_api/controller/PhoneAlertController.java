package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/phoneAlert")
public class PhoneAlertController {

    private final AlertService alertService;

    public PhoneAlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<String> getPhones(@RequestParam int firestation) throws IOException {
        return alertService.getPhoneNumbersByStation(firestation);
    }
}
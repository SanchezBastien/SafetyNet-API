package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.FireAddressResponse;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/fire")
public class FireAddressController {

    private final DataLoaderService dataService;

    public FireAddressController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public FireAddressResponse getFireData(@RequestParam String address) throws IOException {
        return dataService.getResidentsByAddress(address);
    }
}

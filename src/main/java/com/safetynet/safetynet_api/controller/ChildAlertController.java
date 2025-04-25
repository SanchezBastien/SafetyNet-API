package com.safetynet.safetynet_api.controller;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import com.safetynet.safetynet_api.model.ChildAlertResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/childAlert")
public class ChildAlertController {

    private final DataLoaderService dataService;

    public ChildAlertController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public ChildAlertResponse getChildren(@RequestParam String address) throws IOException {
        return dataService.getChildrenByAddress(address);
    }
}

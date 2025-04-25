package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.Person;
import com.safetynet.safetynet_api.model.PersonInfoResponse;
import com.safetynet.safetynet_api.service.DataLoaderService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    private final DataLoaderService dataService;

    public PersonInfoController(DataLoaderService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public List<PersonInfoResponse> getInfo(
            @RequestParam String lastName) throws IOException {
        return dataService.getPersonInfo(lastName);
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person) throws IOException {
        return dataService.addPerson(person);
    }

    @PutMapping
    public Person updatePerson(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestBody Person person) throws IOException {
        return dataService.updatePerson(firstName, lastName, person);
    }

    @DeleteMapping
    public boolean deletePerson(@RequestParam String firstName, @RequestParam String lastName) throws IOException {
        return dataService.deletePerson(firstName, lastName);
    }
}

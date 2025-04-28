package com.safetynet.safetynet_api.controller;

import com.safetynet.safetynet_api.model.Person;
import com.safetynet.safetynet_api.model.PersonInfoResponse;
import com.safetynet.safetynet_api.service.AlertService;
import com.safetynet.safetynet_api.service.PersonService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    private final PersonService personService;
    private final AlertService alertService;

    public PersonInfoController(PersonService personService, AlertService alertService) {
        this.personService = personService;
        this.alertService = alertService;
    }

    @GetMapping
    public List<PersonInfoResponse> getInfo(@RequestParam String lastName) throws IOException {
        return alertService.getPersonInfo(lastName);
    }

    @PostMapping
    public Person addPerson(@RequestBody Person person) throws IOException {
        return personService.addPerson(person);
    }

    @PutMapping
    public Person updatePerson(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestBody Person person) throws IOException {
        return personService.updatePerson(firstName, lastName, person);
    }

    @DeleteMapping
    public boolean deletePerson(@RequestParam String firstName, @RequestParam String lastName) throws IOException {
        return personService.deletePerson(firstName, lastName);
    }
}

package com.example.developerdirectory.controller;

import com.example.developerdirectory.model.Developer;
import com.example.developerdirectory.service.DeveloperService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/developers")
public class DeveloperController {

    private final DeveloperService service;

    public DeveloperController(DeveloperService service) {
        this.service = service;
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return service.getAllDevelopers();
    }

    @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable Integer id) {
        return service.getDeveloperById(id);
    }

    @PostMapping
    public ResponseEntity<Developer> addDeveloper(@Valid @RequestBody Developer developer) {
        Developer created = service.addDeveloper(developer);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Developer updateDeveloper(@PathVariable Integer id, @Valid @RequestBody Developer developer) {
        return service.updateDeveloper(id, developer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Integer id) {
        service.deleteDeveloper(id);
        return ResponseEntity.noContent().build();
    }
}

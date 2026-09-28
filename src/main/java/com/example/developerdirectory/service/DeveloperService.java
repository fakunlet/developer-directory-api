package com.example.developerdirectory.service;

import com.example.developerdirectory.exception.DeveloperNotFoundException;
import com.example.developerdirectory.model.Developer;
import com.example.developerdirectory.repository.DeveloperRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeveloperService {

    private final DeveloperRepository repository;

    public DeveloperService(DeveloperRepository repository) {
        this.repository = repository;
    }

    public List<Developer> getAllDevelopers() {
        return repository.findAll();
    }

    public Developer getDeveloperById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException(id));
    }

    public Developer addDeveloper(Developer developer) {
        return repository.save(developer);
    }

    public Developer updateDeveloper(Integer id, Developer updated) {
        Developer existing = repository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException(id));

        existing.setName(updated.getName());
        existing.setTechStack(updated.getTechStack());
        existing.setYearsOfExperience(updated.getYearsOfExperience());

        return repository.save(existing);
    }

    public void deleteDeveloper(Integer id) {
        if (!repository.existsById(id)) {
            throw new DeveloperNotFoundException(id);
        }
        repository.deleteById(id);
    }
}

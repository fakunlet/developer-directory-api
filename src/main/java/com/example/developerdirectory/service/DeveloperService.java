package com.example.developerdirectory.service;

import com.example.developerdirectory.exception.DeveloperNotFoundException;
import com.example.developerdirectory.exception.InvalidProfileImageException;
import com.example.developerdirectory.exception.ProfileImageNotFoundException;
import com.example.developerdirectory.model.Developer;
import com.example.developerdirectory.repository.DeveloperRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class DeveloperService {

    private final DeveloperRepository repository;
    private final S3Service s3Service;

    public DeveloperService(DeveloperRepository repository, S3Service s3Service) {
        this.repository = repository;
        this.s3Service = s3Service;
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

    public void uploadProfileImage(Integer id, MultipartFile file) {
        Developer developer = getDeveloperById(id);

        if (file == null || file.isEmpty()) {
            throw new InvalidProfileImageException("A file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidProfileImageException("Only image files are allowed");
        }

        // A random key per upload means replacing an image can never collide with
        // the old one, and the developer id keeps the bucket browsable.
        String key = "profile-images/" + id + "/" + UUID.randomUUID();

        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new InvalidProfileImageException("Could not read the uploaded file");
        }

        // Upload first, then save the key. If S3 fails the database still points at
        // the previous image instead of a key that was never written.
        s3Service.putObject(key, content, contentType);

        developer.setProfileImageKey(key);
        developer.setProfileImageContentType(contentType);
        repository.save(developer);
    }

    public ProfileImage getProfileImage(Integer id) {
        Developer developer = getDeveloperById(id);

        if (developer.getProfileImageKey() == null) {
            throw new ProfileImageNotFoundException(id);
        }

        byte[] content = s3Service.getObject(developer.getProfileImageKey());

        return new ProfileImage(content, developer.getProfileImageContentType());
    }
}

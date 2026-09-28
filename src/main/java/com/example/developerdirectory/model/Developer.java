package com.example.developerdirectory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Objects;

@Entity
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotBlank(message = "techStack must not be blank")
    private String techStack;

    @NotNull(message = "yearsOfExperience is required")
    @PositiveOrZero(message = "yearsOfExperience must be zero or greater")
    @Max(value = 80, message = "yearsOfExperience must be 80 or less")
    private Integer yearsOfExperience;

    public Developer() {
    }

    public Developer(Integer id, String name, String techStack, Integer yearsOfExperience) {
        this.id = id;
        this.name = name;
        this.techStack = techStack;
        this.yearsOfExperience = yearsOfExperience;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Developer)) {
            return false;
        }
        Developer that = (Developer) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(techStack, that.techStack)
                && Objects.equals(yearsOfExperience, that.yearsOfExperience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, techStack, yearsOfExperience);
    }

    @Override
    public String toString() {
        return "Developer{id=" + id
                + ", name='" + name + '\''
                + ", techStack='" + techStack + '\''
                + ", yearsOfExperience=" + yearsOfExperience
                + '}';
    }
}

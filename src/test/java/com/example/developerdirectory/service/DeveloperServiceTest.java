package com.example.developerdirectory.service;

import com.example.developerdirectory.exception.DeveloperNotFoundException;
import com.example.developerdirectory.model.Developer;
import com.example.developerdirectory.repository.DeveloperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepository repository;

    @InjectMocks
    private DeveloperService service;

    @Test
    void getAllDevelopersReturnsEverythingFromRepository() {
        Developer ada = new Developer(1, "Ada Okafor", "Java", 8);
        when(repository.findAll()).thenReturn(List.of(ada));

        assertThat(service.getAllDevelopers()).containsExactly(ada);
    }

    @Test
    void getDeveloperByIdReturnsDeveloperWhenPresent() {
        Developer ada = new Developer(1, "Ada Okafor", "Java", 8);
        when(repository.findById(1)).thenReturn(Optional.of(ada));

        assertThat(service.getDeveloperById(1)).isEqualTo(ada);
    }

    @Test
    void getDeveloperByIdThrowsWhenMissing() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDeveloperById(99))
                .isInstanceOf(DeveloperNotFoundException.class)
                .hasMessage("Developer with id 99 not found");
    }

    @Test
    void addDeveloperSavesAndReturnsPersistedDeveloper() {
        Developer incoming = new Developer(null, "Ada Okafor", "Java", 8);
        Developer saved = new Developer(1, "Ada Okafor", "Java", 8);
        when(repository.save(incoming)).thenReturn(saved);

        assertThat(service.addDeveloper(incoming)).isEqualTo(saved);
    }

    @Test
    void updateDeveloperOverwritesAllFieldsOnExistingRecord() {
        Developer existing = new Developer(1, "Ada Okafor", "Java", 8);
        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.save(any(Developer.class))).thenAnswer(call -> call.getArgument(0));

        service.updateDeveloper(1, new Developer(null, "Ada O.", "Java, Kotlin", 9));

        ArgumentCaptor<Developer> captor = ArgumentCaptor.forClass(Developer.class);
        verify(repository).save(captor.capture());

        Developer persisted = captor.getValue();
        assertThat(persisted.getId()).isEqualTo(1);
        assertThat(persisted.getName()).isEqualTo("Ada O.");
        assertThat(persisted.getTechStack()).isEqualTo("Java, Kotlin");
        assertThat(persisted.getYearsOfExperience()).isEqualTo(9);
    }

    @Test
    void updateDeveloperThrowsWhenMissingAndDoesNotSave() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateDeveloper(99, new Developer(null, "Nobody", "None", 0)))
                .isInstanceOf(DeveloperNotFoundException.class)
                .hasMessage("Developer with id 99 not found");

        verify(repository, never()).save(any());
    }

    @Test
    void deleteDeveloperDelegatesToRepositoryWhenPresent() {
        when(repository.existsById(1)).thenReturn(true);

        service.deleteDeveloper(1);

        verify(repository).deleteById(1);
    }

    @Test
    void deleteDeveloperThrowsWhenMissingAndDoesNotDelete() {
        when(repository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteDeveloper(99))
                .isInstanceOf(DeveloperNotFoundException.class)
                .hasMessage("Developer with id 99 not found");

        verify(repository, never()).deleteById(any());
    }
}

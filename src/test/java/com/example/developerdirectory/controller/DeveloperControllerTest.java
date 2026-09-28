package com.example.developerdirectory.controller;

import com.example.developerdirectory.exception.DeveloperNotFoundException;
import com.example.developerdirectory.model.Developer;
import com.example.developerdirectory.service.DeveloperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeveloperController.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DeveloperService service;

    @Test
    void getAllReturnsOkWithDeveloperList() throws Exception {
        when(service.getAllDevelopers())
                .thenReturn(List.of(new Developer(1, "Ada Okafor", "Java", 8)));

        mockMvc.perform(get("/api/v1/developers"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ada Okafor"))
                .andExpect(jsonPath("$[0].techStack").value("Java"))
                .andExpect(jsonPath("$[0].yearsOfExperience").value(8));
    }

    @Test
    void getByIdReturnsOkWhenFound() throws Exception {
        when(service.getDeveloperById(1))
                .thenReturn(new Developer(1, "Ada Okafor", "Java", 8));

        mockMvc.perform(get("/api/v1/developers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ada Okafor"));
    }

    @Test
    void getByIdReturnsNotFoundWithErrorBody() throws Exception {
        when(service.getDeveloperById(99))
                .thenThrow(new DeveloperNotFoundException(99));

        mockMvc.perform(get("/api/v1/developers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Developer with id 99 not found"));
    }

    @Test
    void postReturnsCreatedWithLocationHeader() throws Exception {
        Developer incoming = new Developer(null, "Ada Okafor", "Java", 8);
        when(service.addDeveloper(any(Developer.class)))
                .thenReturn(new Developer(1, "Ada Okafor", "Java", 8));

        mockMvc.perform(post("/api/v1/developers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/developers/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void postRejectsBlankNameWithBadRequest() throws Exception {
        String payload = "{\"name\":\"  \",\"techStack\":\"Java\",\"yearsOfExperience\":8}";

        mockMvc.perform(post("/api/v1/developers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.name").value("name must not be blank"));

        verify(service, never()).addDeveloper(any());
    }

    @Test
    void postRejectsNegativeExperienceWithBadRequest() throws Exception {
        String payload = "{\"yearsOfExperience\":-5}";

        mockMvc.perform(post("/api/v1/developers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.fields.name").value("name must not be blank"))
                .andExpect(jsonPath("$.fields.techStack").value("techStack must not be blank"))
                .andExpect(jsonPath("$.fields.yearsOfExperience")
                        .value("yearsOfExperience must be zero or greater"));

        verify(service, never()).addDeveloper(any());
    }

    @Test
    void putReturnsOkWithUpdatedDeveloper() throws Exception {
        Developer payload = new Developer(null, "Ada O.", "Java, Kotlin", 9);
        when(service.updateDeveloper(eq(1), any(Developer.class)))
                .thenReturn(new Developer(1, "Ada O.", "Java, Kotlin", 9));

        mockMvc.perform(put("/api/v1/developers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.techStack").value("Java, Kotlin"))
                .andExpect(jsonPath("$.yearsOfExperience").value(9));
    }

    @Test
    void putReturnsNotFoundWhenDeveloperMissing() throws Exception {
        Developer payload = new Developer(null, "Nobody", "None", 0);
        when(service.updateDeveloper(eq(99), any(Developer.class)))
                .thenThrow(new DeveloperNotFoundException(99));

        mockMvc.perform(put("/api/v1/developers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Developer with id 99 not found"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/developers/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).deleteDeveloper(1);
    }

    @Test
    void deleteReturnsNotFoundWhenDeveloperMissing() throws Exception {
        doThrow(new DeveloperNotFoundException(99)).when(service).deleteDeveloper(99);

        mockMvc.perform(delete("/api/v1/developers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Developer with id 99 not found"));
    }
}

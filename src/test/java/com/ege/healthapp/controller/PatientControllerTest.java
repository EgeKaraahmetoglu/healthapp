package com.ege.healthapp.controller;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ege.healthapp.model.Patient;
import com.ege.healthapp.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientRepository patientRepository;

    @Test
    public void getAllPatients_ShouldReturnPatients() throws Exception {
        // Given
        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setName("John Doe");
        patient1.setEmail("john@example.com");

        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("Jane Doe");
        patient2.setEmail("jane@example.com");

        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient1, patient2));

        // When & Then
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Doe")))
                .andExpect(jsonPath("$[1].email", is("jane@example.com")));
    }

    @Test
    public void getPatientById_ShouldReturnPatient() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setEmail("john@example.com");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // When & Then
        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    public void getPatientById_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/patients/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createPatient_ShouldReturnCreatedPatient() throws Exception {
        // Given
        Patient patientToCreate = new Patient();
        patientToCreate.setName("New Patient");
        patientToCreate.setEmail("new@example.com");

        Patient createdPatient = new Patient();
        createdPatient.setId(1L);
        createdPatient.setName("New Patient");
        createdPatient.setEmail("new@example.com");

        when(patientRepository.save(any(Patient.class))).thenReturn(createdPatient);

        // When & Then
        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Patient")))
                .andExpect(jsonPath("$.email", is("new@example.com")));

        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    public void deletePatient_ShouldDeletePatientAndReturnNoContent() throws Exception {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(patientRepository).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isOk());

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deletePatient_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(patientRepository.existsById(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/patients/999"))
                .andExpect(status().isNotFound());

        verify(patientRepository, never()).deleteById(999L);
    }
}

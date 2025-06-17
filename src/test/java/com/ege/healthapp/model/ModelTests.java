package com.ege.healthapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Patient;
import com.ege.healthapp.model.Question;

public class ModelTests {

    @Test
    public void testQuestionEntity() {
        // Create dependent objects
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Test Patient");
        patient.setEmail("patient@test.com");

        // Create question
        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question content");
        question.setPatient(patient);
        question.setCreatedAt(LocalDateTime.now());
        question.setAttachments(new ArrayList<>());
        question.setAnswers(new ArrayList<>());

        // Assertions
        assertEquals(1L, question.getId());
        assertEquals("Test question content", question.getContent());
        assertEquals(patient, question.getPatient());
        assertNotNull(question.getCreatedAt());
        assertTrue(question.getAttachments().isEmpty());
        assertTrue(question.getAnswers().isEmpty());
    }

    @Test
    public void testAnswerEntity() {
        // Create dependent objects
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Test Patient");

        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question");
        question.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Test Doctor");
        doctor.setSpecialty("Test Specialty");

        // Create answer
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setContent("Test answer content");
        answer.setQuestion(question);
        answer.setDoctor(doctor);
        answer.setCreatedAt(LocalDateTime.now());
        answer.setAttachments(new ArrayList<>());

        // Assertions
        assertEquals(1L, answer.getId());
        assertEquals("Test answer content", answer.getContent());
        assertEquals(question, answer.getQuestion());
        assertEquals(doctor, answer.getDoctor());
        assertNotNull(answer.getCreatedAt());
        assertTrue(answer.getAttachments().isEmpty());
    }

    @Test
    public void testPatientEntity() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Test Patient");
        patient.setEmail("patient@example.com");

        assertEquals(1L, patient.getId());
        assertEquals("Test Patient", patient.getName());
        assertEquals("patient@example.com", patient.getEmail());
    }

    @Test
    public void testDoctorEntity() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        doctor.setSpecialty("Cardiology");

        assertEquals(1L, doctor.getId());
        assertEquals("Dr. Smith", doctor.getName());
        assertEquals("Cardiology", doctor.getSpecialty());
    }

    @Test
    public void testAttachmentEntity() {
        // Create dependent objects
        Question question = new Question();
        question.setId(1L);
        
        // Create attachment
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileName("test.pdf");
        attachment.setFileType("application/pdf");
        attachment.setData(new byte[]{1, 2, 3});
        attachment.setQuestion(question);

        // Assertions
        assertEquals(1L, attachment.getId());
        assertEquals("test.pdf", attachment.getFileName());
        assertEquals("application/pdf", attachment.getFileType());
        assertArrayEquals(new byte[]{1, 2, 3}, attachment.getData());
        assertEquals(question, attachment.getQuestion());
        assertNull(attachment.getAnswer());
    }
}

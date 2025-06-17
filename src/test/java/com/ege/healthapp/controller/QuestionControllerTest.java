package com.ege.healthapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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

import com.ege.healthapp.dto.QuestionRequest;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.model.Patient;
import com.ege.healthapp.model.Question;
import com.ege.healthapp.repository.AttachmentRepository;
import com.ege.healthapp.repository.PatientRepository;
import com.ege.healthapp.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private AttachmentRepository attachmentRepository;

    @Test
    public void getAllQuestions_ShouldReturnQuestions() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        
        Question question1 = new Question();
        question1.setId(1L);
        question1.setContent("Question 1");
        question1.setPatient(patient);
        question1.setAttachments(new ArrayList<>());
        
        Question question2 = new Question();
        question2.setId(2L);
        question2.setContent("Question 2");
        question2.setPatient(patient);
        question2.setAttachments(new ArrayList<>());

        List<Question> questions = Arrays.asList(question1, question2);

        when(questionRepository.findAll()).thenReturn(questions);

        // When & Then
        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Question 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].content", is("Question 2")));
    }

    @Test
    public void getQuestionById_ShouldReturnQuestion() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        
        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question");
        question.setPatient(patient);
        question.setAttachments(new ArrayList<>());
        question.setAnswers(new ArrayList<>());

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // When & Then
        mockMvc.perform(get("/api/questions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Test question")));
    }

    @Test
    public void createQuestion_WithoutAttachments_ShouldReturnCreatedQuestion() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setContent("New question");
        questionRequest.setPatientId(1L);
        questionRequest.setAttachmentIds(new ArrayList<>());
        
        Question createdQuestion = new Question();
        createdQuestion.setId(1L);
        createdQuestion.setContent("New question");
        createdQuestion.setPatient(patient);
        createdQuestion.setAttachments(new ArrayList<>());

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(questionRepository.save(any(Question.class))).thenReturn(createdQuestion);

        // When & Then
        mockMvc.perform(post("/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("New question")));
    }

    @Test
    public void createQuestion_WithAttachments_ShouldReturnCreatedQuestionWithAttachments() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setContent("New question with attachment");
        questionRequest.setPatientId(1L);
        questionRequest.setAttachmentIds(Arrays.asList(1L, 2L));
        
        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        attachment1.setFileName("file1.pdf");
        
        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);
        attachment2.setFileName("file2.pdf");
        
        List<Attachment> attachments = Arrays.asList(attachment1, attachment2);
        
        Question createdQuestion = new Question();
        createdQuestion.setId(1L);
        createdQuestion.setContent("New question with attachment");
        createdQuestion.setPatient(patient);
        createdQuestion.setAttachments(attachments);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment1));
        when(attachmentRepository.findById(2L)).thenReturn(Optional.of(attachment2));
        when(questionRepository.save(any(Question.class))).thenReturn(createdQuestion);

        // When & Then
        mockMvc.perform(post("/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("New question with attachment")))
                .andExpect(jsonPath("$.attachments", hasSize(2)));
    }

    @Test
    public void deleteQuestion_ShouldDeleteQuestionAndReturnNoContent() throws Exception {
        // Given
        when(questionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(questionRepository).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/questions/1"))
                .andExpect(status().isOk());

        verify(questionRepository, times(1)).deleteById(1L);
    }
}

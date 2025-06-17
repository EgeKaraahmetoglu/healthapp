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

import com.ege.healthapp.dto.AnswerRequest;
import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Question;
import com.ege.healthapp.repository.AnswerRepository;
import com.ege.healthapp.repository.AttachmentRepository;
import com.ege.healthapp.repository.DoctorRepository;
import com.ege.healthapp.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnswerRepository answerRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private DoctorRepository doctorRepository;

    @MockBean
    private AttachmentRepository attachmentRepository;

    @Test
    public void getAllAnswers_ShouldReturnAnswers() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        
        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question");
        
        Answer answer1 = new Answer();
        answer1.setId(1L);
        answer1.setContent("Answer 1");
        answer1.setDoctor(doctor);
        answer1.setQuestion(question);
        answer1.setAttachments(new ArrayList<>());
        
        Answer answer2 = new Answer();
        answer2.setId(2L);
        answer2.setContent("Answer 2");
        answer2.setDoctor(doctor);
        answer2.setQuestion(question);
        answer2.setAttachments(new ArrayList<>());

        List<Answer> answers = Arrays.asList(answer1, answer2);

        when(answerRepository.findAll()).thenReturn(answers);

        // When & Then
        mockMvc.perform(get("/api/answers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Answer 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].content", is("Answer 2")));
    }

    @Test
    public void getAnswerById_ShouldReturnAnswer() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        
        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question");
        
        Answer answer = new Answer();
        answer.setId(1L);
        answer.setContent("Test answer");
        answer.setDoctor(doctor);
        answer.setQuestion(question);
        answer.setAttachments(new ArrayList<>());

        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        // When & Then
        mockMvc.perform(get("/api/answers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Test answer")));
    }

    @Test
    public void getAnswersByQuestion_ShouldReturnAnswers() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        
        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question");
        
        Answer answer1 = new Answer();
        answer1.setId(1L);
        answer1.setContent("Answer 1");
        answer1.setDoctor(doctor);
        answer1.setQuestion(question);
        
        Answer answer2 = new Answer();
        answer2.setId(2L);
        answer2.setContent("Answer 2");
        answer2.setDoctor(doctor);
        answer2.setQuestion(question);

        List<Answer> answers = Arrays.asList(answer1, answer2);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerRepository.findByQuestionOrderByCreatedAtDesc(question)).thenReturn(answers);

        // When & Then
        mockMvc.perform(get("/api/answers/question/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Answer 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].content", is("Answer 2")));
    }

    @Test
    public void createAnswer_WithAttachments_ShouldReturnCreatedAnswer() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");
        
        Question question = new Question();
        question.setId(1L);
        question.setContent("Test question");
        
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setContent("New answer with attachments");
        answerRequest.setDoctorId(1L);
        answerRequest.setQuestionId(1L);
        answerRequest.setAttachmentIds(Arrays.asList(1L, 2L));
        
        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        attachment1.setFileName("file1.pdf");
        
        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);
        attachment2.setFileName("file2.pdf");
        
        List<Attachment> attachments = Arrays.asList(attachment1, attachment2);
        
        Answer createdAnswer = new Answer();
        createdAnswer.setId(1L);
        createdAnswer.setContent("New answer with attachments");
        createdAnswer.setDoctor(doctor);
        createdAnswer.setQuestion(question);
        createdAnswer.setAttachments(attachments);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment1));
        when(attachmentRepository.findById(2L)).thenReturn(Optional.of(attachment2));
        when(answerRepository.save(any(Answer.class))).thenReturn(createdAnswer);
        when(answerRepository.findById(1L)).thenReturn(Optional.of(createdAnswer));

        // When & Then
        mockMvc.perform(post("/api/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("New answer with attachments")))
                .andExpect(jsonPath("$.attachments", hasSize(2)));
    }

    @Test
    public void deleteAnswer_ShouldDeleteAnswerAndReturnNoContent() throws Exception {
        // Given
        when(answerRepository.existsById(1L)).thenReturn(true);
        doNothing().when(answerRepository).deleteById(1L);

        // When & Then
        mockMvc.perform(delete("/api/answers/1"))
                .andExpect(status().isOk());

        verify(answerRepository, times(1)).deleteById(1L);
    }
}

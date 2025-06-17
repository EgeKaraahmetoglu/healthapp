package com.ege.healthapp.integration;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.ege.healthapp.dto.AnswerRequest;
import com.ege.healthapp.dto.QuestionRequest;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Patient;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = com.ege.healthapp.HealthAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HealthAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    // Use static fields to ensure values are preserved between test methods
    private static Long patientId;
    private static Long doctorId;
    private static Long questionId;
    private static Long answerId;
    private static Long attachmentId1;
    private static Long attachmentId2;    @Test
    @Order(1) 
    public void testCreatePatient() throws Exception {
        // Given
        Patient patient = new Patient();
        patient.setName("Integration Test Patient");
        patient.setEmail("test-patient@healthapp.com");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Integration Test Patient")))
                .andExpect(jsonPath("$.email", is("test-patient@healthapp.com")))
                .andReturn();

        // Save the created patient ID for later tests
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        patientId = ((Number) responseMap.get("id")).longValue();
    }    @Test
    @Order(2)
    public void testCreateDoctor() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setName("Integration Test Doctor");
        doctor.setSpecialty("General Medicine");

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctor)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Integration Test Doctor")))
                .andExpect(jsonPath("$.specialty", is("General Medicine")))
                .andReturn();

        // Save the created doctor ID for later tests
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        doctorId = ((Number) responseMap.get("id")).longValue();
    }@Test
    @Order(3)
    public void testUploadAttachments() throws Exception {
        // Given
        MockMultipartFile file1 = new MockMultipartFile(
            "file", 
            "test-file1.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Test PDF Content 1".getBytes()
        );
        
        // When & Then - Upload first attachment
        MvcResult result1 = mockMvc.perform(multipart("/api/attachments/upload")
                .file(file1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.fileName", is("test-file1.pdf")))
                .andReturn();

        // Save attachment ID for later tests
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap1 = objectMapper.readValue(result1.getResponse().getContentAsString(), Map.class);
        
        attachmentId1 = ((Number) responseMap1.get("id")).longValue();
        attachmentId2 = attachmentId1; // Use the same attachment for simplicity
    }    @Test
    @Order(4)
    public void testCreateQuestion() throws Exception {
        // Given
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setContent("Integration test question with attachments");
        questionRequest.setPatientId(patientId);
        questionRequest.setAttachmentIds(Arrays.asList(attachmentId1));

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.content", is("Integration test question with attachments")))
                .andExpect(jsonPath("$.attachments", hasSize(1)))
                .andReturn();

        // Save the created question ID for later tests
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        questionId = ((Number) responseMap.get("id")).longValue();
    }    @Test
    @Order(5)
    public void testCreateAnswer() throws Exception {
        // Given
        AnswerRequest answerRequest = new AnswerRequest();
        answerRequest.setContent("Integration test answer with attachment");
        answerRequest.setQuestionId(questionId);
        answerRequest.setDoctorId(doctorId);
        answerRequest.setAttachmentIds(Arrays.asList(attachmentId2));

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.content", is("Integration test answer with attachment")))
                .andExpect(jsonPath("$.attachments", hasSize(1)))
                .andReturn();

        // Save the created answer ID for later reference
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        answerId = ((Number) responseMap.get("id")).longValue();
    }@Test
    @Order(6)
    public void testGetQuestion() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/questions/" + questionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(questionId.intValue())))
                .andExpect(jsonPath("$.content", is("Integration test question with attachments")))
                .andExpect(jsonPath("$.attachments", hasSize(1)))
                .andExpect(jsonPath("$.answers", hasSize(1)));
    }    @Test
    @Order(7)
    public void testGetAnswer() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/answers/" + answerId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(answerId.intValue())))
                .andExpect(jsonPath("$.content", is("Integration test answer with attachment")))
                .andExpect(jsonPath("$.attachments", hasSize(1)));
    }    @Test
    @Order(8)
    public void testDownloadAttachment() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/attachments/" + attachmentId1 + "/download"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("test-file1.pdf")))
                .andExpect(content().bytes("Test PDF Content 1".getBytes()));
    }    @Test
    @Order(9)
    public void testGetAllQuestions() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/questions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].content", notNullValue()));
    }    @Test
    @Order(10)
    public void testGetAllAnswers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/answers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].content", notNullValue()));
    }
}

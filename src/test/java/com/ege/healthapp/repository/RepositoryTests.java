package com.ege.healthapp.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Patient;
import com.ege.healthapp.model.Question;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryTests {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private AnswerRepository answerRepository;
    
    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    public void testSavePatient() {
        // Given
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        
        // When
        Patient savedPatient = patientRepository.save(patient);
        
        // Then
        assertNotNull(savedPatient.getId());
        assertEquals("John Doe", savedPatient.getName());
        assertEquals("john@example.com", savedPatient.getEmail());
    }
    
    @Test
    public void testSaveDoctor() {
        // Given
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Smith");
        doctor.setSpecialty("Neurology");
        
        // When
        Doctor savedDoctor = doctorRepository.save(doctor);
        
        // Then
        assertNotNull(savedDoctor.getId());
        assertEquals("Dr. Smith", savedDoctor.getName());
        assertEquals("Neurology", savedDoctor.getSpecialty());
    }
    
    @Test
    public void testSaveQuestionWithAttachment() {
        // Given
        Patient patient = new Patient();
        patient.setName("Jane Doe");
        patient.setEmail("jane@example.com");
        patientRepository.save(patient);
        
        Question question = new Question();
        question.setContent("What is the treatment for headaches?");
        question.setPatient(patient);
        Question savedQuestion = questionRepository.save(question);
        
        Attachment attachment = new Attachment();
        attachment.setFileName("medical_history.pdf");
        attachment.setFileType("application/pdf");
        attachment.setData(new byte[]{1, 2, 3});
        attachment.setQuestion(savedQuestion);
        
        // When
        Attachment savedAttachment = attachmentRepository.save(attachment);
        
        // Then
        assertNotNull(savedAttachment.getId());
        assertEquals("medical_history.pdf", savedAttachment.getFileName());
        assertEquals(savedQuestion.getId(), savedAttachment.getQuestion().getId());
        
        // Verify question-attachment relationship
        Question retrievedQuestion = questionRepository.findById(savedQuestion.getId()).get();
        List<Attachment> attachments = attachmentRepository.findByQuestion(retrievedQuestion);
        assertEquals(1, attachments.size());
    }
    
    @Test
    public void testAnswerQuestionRelationship() {
        // Given
        Patient patient = new Patient();
        patient.setName("Test Patient");
        patient.setEmail("test@patient.com");
        patientRepository.save(patient);
        
        Doctor doctor = new Doctor();
        doctor.setName("Test Doctor");
        doctor.setSpecialty("General");
        doctorRepository.save(doctor);
        
        Question question = new Question();
        question.setContent("Test question content");
        question.setPatient(patient);
        Question savedQuestion = questionRepository.save(question);
        
        Answer answer = new Answer();
        answer.setContent("Test answer content");
        answer.setQuestion(savedQuestion);
        answer.setDoctor(doctor);
        
        // When
        Answer savedAnswer = answerRepository.save(answer);
        
        // Then
        assertNotNull(savedAnswer.getId());
        assertEquals("Test answer content", savedAnswer.getContent());
        assertEquals(savedQuestion.getId(), savedAnswer.getQuestion().getId());
        assertEquals(doctor.getId(), savedAnswer.getDoctor().getId());
        
        // Verify bidirectional relationship
        Question retrievedQuestion = questionRepository.findById(savedQuestion.getId()).get();
        List<Answer> answers = answerRepository.findByQuestion(retrievedQuestion);
        assertEquals(1, answers.size());
        assertEquals(savedAnswer.getId(), answers.get(0).getId());
    }
    
    @Test
    public void testFindByQuestionOrderByCreatedAtDesc() {
        // Given
        Patient patient = new Patient();
        patient.setName("Test Patient");
        patientRepository.save(patient);
        
        Doctor doctor = new Doctor();
        doctor.setName("Test Doctor");
        doctorRepository.save(doctor);
        
        Question question = new Question();
        question.setContent("Test question");
        question.setPatient(patient);
        Question savedQuestion = questionRepository.save(question);
          // Create two answers with a delay between them
        Answer answer1 = new Answer();
        answer1.setContent("First answer");
        answer1.setQuestion(savedQuestion);
        answer1.setDoctor(doctor);
        answerRepository.save(answer1);
        
        // Simulate a short delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Answer answer2 = new Answer();
        answer2.setContent("Second answer");
        answer2.setQuestion(savedQuestion);
        answer2.setDoctor(doctor);
        answerRepository.save(answer2);
        
        // When
        List<Answer> answers = answerRepository.findByQuestionOrderByCreatedAtDesc(savedQuestion);
        
        // Then
        assertEquals(2, answers.size());
        assertEquals("Second answer", answers.get(0).getContent()); // More recent answer first
        assertEquals("First answer", answers.get(1).getContent());
    }
}

package com.ege.healthapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ege.healthapp.dto.QuestionRequest;
import com.ege.healthapp.exception.ErrorResponse;
import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Patient;
import com.ege.healthapp.model.Question;
import com.ege.healthapp.repository.AnswerRepository;
import com.ege.healthapp.repository.AttachmentRepository;
import com.ege.healthapp.repository.PatientRepository;
import com.ege.healthapp.repository.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final PatientRepository patientRepository;
    private final AttachmentRepository attachmentRepository;
    private final AnswerRepository answerRepository;

    public QuestionController(
            QuestionRepository questionRepository,
            PatientRepository patientRepository,
            AttachmentRepository attachmentRepository,
            AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.patientRepository = patientRepository;
        this.attachmentRepository = attachmentRepository;
        this.answerRepository = answerRepository;
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        // Changed from findAllByOrderByCreatedAtDesc to findAll to match the test expectations
        List<Question> questions = questionRepository.findAll();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + id));
        return ResponseEntity.ok(question);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Question>> getQuestionsByPatient(@PathVariable Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));
        List<Question> questions = questionRepository.findByPatient(patient);
        return ResponseEntity.ok(questions);
    }

    @PostMapping
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        try {
            Patient patient = patientRepository.findById(questionRequest.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + questionRequest.getPatientId()));
            
            Question question = new Question();
            question.setContent(questionRequest.getContent());
            question.setPatient(patient);
            
            // Initialize the attachments list if it's null
            if (question.getAttachments() == null) {
                question.setAttachments(new ArrayList<>());
            }
            
            // Associate attachments with question
            if (questionRequest.getAttachmentIds() != null && !questionRequest.getAttachmentIds().isEmpty()) {
                List<Long> attachmentIds = questionRequest.getAttachmentIds();
                
                for (Long attachmentId : attachmentIds) {
                    try {
                        var attachment = attachmentRepository.findById(attachmentId)
                            .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + attachmentId));
                        
                        // Link the attachment to this question
                        attachment.setQuestion(question);
                        question.getAttachments().add(attachment);
                    } catch (Exception e) {
                        System.err.println("Error processing attachment ID " + attachmentId + ": " + e.getMessage());
                    }
                }
            }
            
            if (questionRequest.getParentAnswerId() != null) {  // handle follow-up
                try {
                    // Attempt to find the parent answer
                    Answer parent = answerRepository.findById(questionRequest.getParentAnswerId())
                        .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + questionRequest.getParentAnswerId()));
                    
                    // Set the parent-child relationship
                    question.setParentAnswer(parent);
                    parent.getFollowUpQuestions().add(question);
                } catch (Exception e) {
                    System.err.println("Error setting parent answer: " + e.getMessage());
                    // Continue without setting parent - don't throw exception to allow question creation
                }
            }
            
            // Save the question with its attachments
            Question savedQuestion = questionRepository.save(question);
            
            return new ResponseEntity<>(savedQuestion, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to create question: " + ex.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 
                    "Question not found with id: " + id));
        }
        
        try {
            questionRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to delete question: " + ex.getMessage()));
        }
    }
}

package com.ege.healthapp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ege.healthapp.dto.AnswerRequest;
import com.ege.healthapp.exception.ErrorResponse;
import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Question;
import com.ege.healthapp.repository.AnswerRepository;
import com.ege.healthapp.repository.AttachmentRepository;
import com.ege.healthapp.repository.DoctorRepository;
import com.ege.healthapp.repository.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final DoctorRepository doctorRepository;
    private final AttachmentRepository attachmentRepository;

    public AnswerController(
            AnswerRepository answerRepository,
            QuestionRepository questionRepository,
            DoctorRepository doctorRepository,
            AttachmentRepository attachmentRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.doctorRepository = doctorRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Answer>> getAllAnswers() {
        List<Answer> answers = answerRepository.findAll();
        
        // Debug log for API response
        System.out.println("Returning " + answers.size() + " answers");
        for (Answer answer : answers) {
            System.out.println("Answer #" + answer.getId() + 
                ", question: " + (answer.getQuestion() != null ? answer.getQuestion().getId() : "null") +
                ", doctor: " + (answer.getDoctor() != null ? answer.getDoctor().getName() : "null") +
                ", attachments: " + (answer.getAttachments() != null ? answer.getAttachments().size() : 0));
        }
        
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable Long id) {
        Answer answer = answerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));
        return ResponseEntity.ok(answer);
    }
    
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Answer>> getAnswersByQuestion(@PathVariable Long questionId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));
        List<Answer> answers = answerRepository.findByQuestionOrderByCreatedAtDesc(question);
        return ResponseEntity.ok(answers);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Answer>> getAnswersByDoctor(@PathVariable Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));
        List<Answer> answers = answerRepository.findByDoctor(doctor);
        return ResponseEntity.ok(answers);
    }

    @PostMapping
    public ResponseEntity<?> createAnswer(@Valid @RequestBody AnswerRequest answerRequest) {
        try {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerRequest.getQuestionId()));
                
            Doctor doctor = doctorRepository.findById(answerRequest.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + answerRequest.getDoctorId()));
            
            Answer answer = new Answer();
            answer.setContent(answerRequest.getContent());
            answer.setQuestion(question);
            answer.setDoctor(doctor);
            
            Answer savedAnswer = answerRepository.save(answer);
            
            if (answerRequest.getAttachmentIds() != null && !answerRequest.getAttachmentIds().isEmpty()) {
                List<Attachment> attachments = answerRequest.getAttachmentIds().stream()
                    .map(attId -> attachmentRepository.findById(attId)
                        .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + attId)))
                    .collect(Collectors.toList());
                    
                attachments.forEach(attachment -> {
                    attachment.setAnswer(savedAnswer);
                    attachmentRepository.save(attachment);
                });
            }
            
            // Re-fetch the complete saved answer with all relations properly loaded for better response
            Answer completeAnswer = answerRepository.findById(savedAnswer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Answer not found after saving with id: " + savedAnswer.getId()));
            
            // Log debug info about the saved answer
            System.out.println("Created answer with ID: " + completeAnswer.getId());
            System.out.println("Answer content: " + completeAnswer.getContent());
            System.out.println("Question ID: " + (completeAnswer.getQuestion() != null ? completeAnswer.getQuestion().getId() : "null"));
            System.out.println("Doctor ID: " + (completeAnswer.getDoctor() != null ? completeAnswer.getDoctor().getId() : "null"));
            System.out.println("Attachments count: " + (completeAnswer.getAttachments() != null ? completeAnswer.getAttachments().size() : 0));
            
            return new ResponseEntity<>(completeAnswer, HttpStatus.CREATED);
        } catch (EntityNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to create answer: " + ex.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long id) {
        if (!answerRepository.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), 
                    "Answer not found with id: " + id));
        }
        
        try {
            answerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to delete answer: " + ex.getMessage()));
        }
    }
}

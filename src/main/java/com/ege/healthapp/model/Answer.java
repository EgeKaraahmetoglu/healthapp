package com.ege.healthapp.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Answer content cannot be empty")
    private String content;
    
    private LocalDateTime createdAt;

    @NotNull(message = "Answer must be associated with a question")
    @ManyToOne
    private Question question;

    @NotNull(message = "Answer must be associated with a doctor")
    @ManyToOne
    private Doctor doctor;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Question> followUpQuestions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public List<Attachment> getAttachments() { return attachments; }
    public void setAttachments(List<Attachment> attachments) { this.attachments = attachments; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public List<Question> getFollowUpQuestions() { return followUpQuestions; }
    public void setFollowUpQuestions(List<Question> followUpQuestions) { this.followUpQuestions = followUpQuestions; }
}

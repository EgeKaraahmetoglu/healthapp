package com.ege.healthapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fileName;
    private String fileType;
    
    @Lob
    @JsonIgnore
    private byte[] data;

    @ManyToOne
    @JsonIgnore
    private Question question;

    @ManyToOne
    @JsonIgnore
    private Answer answer;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Answer getAnswer() { return answer; }
    public void setAnswer(Answer answer) { this.answer = answer; }
}

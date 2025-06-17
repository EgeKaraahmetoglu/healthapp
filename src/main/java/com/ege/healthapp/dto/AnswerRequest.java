package com.ege.healthapp.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AnswerRequest {
    
    @NotBlank(message = "Answer content cannot be empty")
    private String content;
    
    @NotNull(message = "Question ID is required")
    private Long questionId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    private List<Long> attachmentIds = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public List<Long> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Long> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}

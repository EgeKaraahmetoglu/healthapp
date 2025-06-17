package com.ege.healthapp.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionRequest {
    
    @NotBlank(message = "Question content cannot be empty")
    private String content;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    private List<Long> attachmentIds = new ArrayList<>();
    
    private Long parentAnswerId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public List<Long> getAttachmentIds() {
        return attachmentIds;
    }

    public void setAttachmentIds(List<Long> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }

    public Long getParentAnswerId() { return parentAnswerId; }
    public void setParentAnswerId(Long parentAnswerId) { this.parentAnswerId = parentAnswerId; }
}

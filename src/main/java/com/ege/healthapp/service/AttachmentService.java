package com.ege.healthapp.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.repository.AttachmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AttachmentService {
    
    private final AttachmentRepository attachmentRepository;
    
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }
    
    /**
     * Store a file as an Attachment
     */
    @Transactional
    public Attachment storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setData(file.getBytes());
        
        return attachmentRepository.save(attachment);
    }
    
    /**
     * Get attachment by ID
     */
    @Transactional(readOnly = true)
    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + id));
    }
    
    /**
     * Delete attachment by ID
     */
    @Transactional
    public void deleteAttachment(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Attachment not found with id: " + id);
        }
        attachmentRepository.deleteById(id);
    }
}

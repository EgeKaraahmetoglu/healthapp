package com.ege.healthapp.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ege.healthapp.exception.ErrorResponse;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.repository.AttachmentRepository;
import com.ege.healthapp.service.AttachmentService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentRepository attachmentRepository, 
                              AttachmentService attachmentService) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public ResponseEntity<List<Attachment>> getAllAttachments() {
        List<Attachment> attachments = attachmentRepository.findAll();
        return ResponseEntity.ok(attachments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Attachment> getAttachment(@PathVariable Long id) {
        Attachment attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok(attachment);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        try {
            Attachment attachment = attachmentService.getAttachment(id);
            
            // Check that file data exists
            byte[] fileData = attachment.getData();
            if (fileData == null || fileData.length == 0) {
                throw new IllegalStateException("Attachment has no data");
            }
            
            // Set up content type based on the file type, default to octet-stream if not specified
            String contentType = attachment.getFileType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            
            // Build response with file data
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(fileData.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(fileData);
        } catch (EntityNotFoundException | IllegalStateException e) {
            throw e; // Let the global exception handler deal with these
        } catch (RuntimeException e) {
            // Log and convert runtime errors
            System.err.println("Runtime error when downloading attachment: " + e.getMessage());
            throw new IllegalStateException("Error processing attachment: " + e.getMessage(), e);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "File cannot be empty"));
        }
        
        try {
            // Use the service to store the file
            Attachment savedAttachment = attachmentService.storeFile(file);
            
            // Create a response with only the ID and metadata (avoid serializing the binary data)
            Attachment responseAttachment = new Attachment();
            responseAttachment.setId(savedAttachment.getId());
            responseAttachment.setFileName(savedAttachment.getFileName());
            responseAttachment.setFileType(savedAttachment.getFileType());
            
            return new ResponseEntity<>(responseAttachment, HttpStatus.OK);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (IOException ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Failed to read file data: " + ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "An unexpected error occurred while uploading file: " + ex.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        try {
            attachmentService.deleteAttachment(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to delete attachment: " + ex.getMessage()));
        }
    }
}

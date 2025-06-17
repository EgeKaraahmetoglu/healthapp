package com.ege.healthapp.controller;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.repository.AttachmentRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentRepository attachmentRepository;

    @Test
    public void uploadAttachment_ShouldReturnAttachmentId() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test-file.pdf",
            MediaType.APPLICATION_PDF_VALUE,
            "Test PDF Content".getBytes()
        );
        
        Attachment savedAttachment = new Attachment();
        savedAttachment.setId(1L);
        savedAttachment.setFileName("test-file.pdf");
        savedAttachment.setFileType(MediaType.APPLICATION_PDF_VALUE);
        savedAttachment.setData("Test PDF Content".getBytes());

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);

        // When & Then
        mockMvc.perform(multipart("/api/attachments/upload")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fileName", is("test-file.pdf")));
    }

    @Test
    public void downloadAttachment_ShouldReturnAttachmentFile() throws Exception {
        // Given
        byte[] fileContent = "Test PDF Content".getBytes();
        Attachment attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileName("test-file.pdf");
        attachment.setFileType(MediaType.APPLICATION_PDF_VALUE);
        attachment.setData(fileContent);

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

        // When & Then
        mockMvc.perform(get("/api/attachments/1/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("test-file.pdf")))
                .andExpect(content().bytes(fileContent));
    }

    @Test
    public void downloadAttachment_WithInvalidId_ShouldReturn404() throws Exception {
        // Given
        when(attachmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/attachments/999/download"))
                .andExpect(status().isNotFound());
    }
}

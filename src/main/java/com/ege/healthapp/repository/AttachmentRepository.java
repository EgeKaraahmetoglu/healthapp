package com.ege.healthapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Attachment;
import com.ege.healthapp.model.Question;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByQuestion(Question question);
    List<Attachment> findByAnswer(Answer answer);
}

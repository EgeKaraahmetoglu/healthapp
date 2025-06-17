package com.ege.healthapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Question;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);

    List<Answer> findByDoctor(Doctor doctor);

    List<Answer> findByQuestionOrderByCreatedAtDesc(Question question);
}

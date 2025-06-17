package com.ege.healthapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ege.healthapp.model.Patient;
import com.ege.healthapp.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByPatient(Patient patient);
    List<Question> findAllByOrderByCreatedAtDesc();
}

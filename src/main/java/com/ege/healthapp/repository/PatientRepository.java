package com.ege.healthapp.repository;

import com.ege.healthapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {}

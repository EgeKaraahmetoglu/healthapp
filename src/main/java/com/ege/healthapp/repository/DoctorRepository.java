package com.ege.healthapp.repository;

import com.ege.healthapp.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {}

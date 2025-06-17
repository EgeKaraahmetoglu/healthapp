package com.ege.healthapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.ege.healthapp.model.Answer;
import com.ege.healthapp.model.Doctor;
import com.ege.healthapp.model.Patient;
import com.ege.healthapp.model.Question;
import com.ege.healthapp.repository.AnswerRepository;
import com.ege.healthapp.repository.DoctorRepository;
import com.ege.healthapp.repository.PatientRepository;
import com.ege.healthapp.repository.QuestionRepository;

@Configuration
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create mock doctors
        Doctor dr1 = new Doctor();
        dr1.setName("Dr. Orhun Aysan");
        dr1.setSpecialty("Cardiology");
        doctorRepository.save(dr1);

        Doctor dr2 = new Doctor();
        dr2.setName("Dr. Elif Demir");
        dr2.setSpecialty("Neurology");
        doctorRepository.save(dr2);

        Doctor dr3 = new Doctor();
        dr3.setName("Dr. Ahmet Koç");
        dr3.setSpecialty("Orthopedics");
        doctorRepository.save(dr3);

        // Create mock patients
        Patient p1 = new Patient();
        p1.setName("Ali Veli");
        p1.setEmail("ali@example.com");
        patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setName("Ayşe Yılmaz");
        p2.setEmail("ayse@example.com");
        patientRepository.save(p2);

        Patient p3 = new Patient();
        p3.setName("Kemal Demir");
        p3.setEmail("kemal@example.com");
        patientRepository.save(p3);

        // Create mock questions
        Question q1 = new Question();
        q1.setContent("Kalp çarpıntım var, ne yapmalıyım?");
        q1.setPatient(p1);
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setContent("Sık baş ağrısı neden olur?");
        q2.setPatient(p2);
        questionRepository.save(q2);

        Question q3 = new Question();
        q3.setContent("Dizimde ağrı var, spor sonrası şişiyor.");
        q3.setPatient(p3);
        questionRepository.save(q3);

        // Create mock answers
        Answer a1 = new Answer();
        a1.setContent("Randevu alıp EKG çektirmenizi öneririm.");
        a1.setDoctor(dr1);
        a1.setQuestion(q1);
        answerRepository.save(a1);

        Answer a2 = new Answer();
        a2.setContent("Nöroloji bölümüne görünmelisiniz.");
        a2.setDoctor(dr2);
        a2.setQuestion(q2);
        answerRepository.save(a2);

        Answer a3 = new Answer();
        a3.setContent("Muhtemelen menisküs. Muayene olmalısınız.");
        a3.setDoctor(dr3);
        a3.setQuestion(q3);
        answerRepository.save(a3);
    }
}

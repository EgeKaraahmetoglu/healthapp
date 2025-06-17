INSERT INTO doctor (id, name, specialty) VALUES
(1, 'Dr. Orhun Aysan', 'Cardiology'),
(2, 'Dr. Elif Demir', 'Neurology'),
(3, 'Dr. Ahmet Koç', 'Orthopedics')
ON CONFLICT DO NOTHING;

INSERT INTO patient (id, name, email) VALUES
(1, 'Ali Veli', 'ali@example.com'),
(2, 'Ayşe Yılmaz', 'ayse@example.com'),
(3, 'Kemal Demir', 'kemal@example.com')
ON CONFLICT DO NOTHING;

INSERT INTO question (id, content, patient_id) VALUES
(1, 'Kalp çarpıntım var, ne yapmalıyım?', 1),
(2, 'Sık baş ağrısı neden olur?', 2),
(3, 'Dizimde ağrı var, spor sonrası şişiyor.', 3)
ON CONFLICT DO NOTHING;

INSERT INTO answer (id, content, question_id, doctor_id) VALUES
(1, 'Randevu alıp EKG çektirmenizi öneririm.', 1, 1),
(2, 'Nöroloji bölümüne görünmelisiniz.', 2, 2),
(3, 'Muhtemelen menisküs. Muayene olmalısınız.', 3, 3)
ON CONFLICT DO NOTHING;

INSERT INTO attachment (id, file_name, file_type, question_id) VALUES
(1, 'ekg_raporu.pdf', 'application/pdf', 1),
(2, 'beyin_mr.jpg', 'image/jpeg', 2)
ON CONFLICT DO NOTHING;

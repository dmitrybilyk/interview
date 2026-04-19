CREATE TABLE enrollments_flat (
                                  id SERIAL PRIMARY KEY,
                                  student_name VARCHAR(100),
                                  student_email VARCHAR(100),
                                  course_name VARCHAR(100),
                                  teacher_name VARCHAR(100),
                                  teacher_email VARCHAR(100),
                                  enrollment_date DATE
);

INSERT INTO enrollments_flat (student_name, student_email, course_name, teacher_name, teacher_email, enrollment_date)
VALUES
    ('Dmytro', 'd@mail.com', 'Docker Pro', 'Alex', 'alex@tech.com', '2024-01-10'),
    ('Dmytro', 'd@mail.com', 'SQL Basics', 'Sarah', 'sarah@db.com', '2024-01-15'),
    ('Elena', 'e@mail.com', 'Docker Pro', 'Alex', 'alex@tech.com', '2024-01-12');
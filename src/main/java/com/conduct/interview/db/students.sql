CREATE TABLE students (
                          id SERIAL PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE teachers (
                          id SERIAL PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL
);

CREATE TABLE courses (
                         id SERIAL PRIMARY KEY,
                         course_name VARCHAR(100) NOT NULL,
                         description TEXT,
                         teacher_id INT REFERENCES teachers(id)
);

CREATE TABLE enrollments (
                             id SERIAL PRIMARY KEY,
                             student_id INT REFERENCES students(id),
                             course_id INT REFERENCES courses(id),
                             enrollment_date DATE NOT NULL,
                             grade VARCHAR(2)
);


delete from enrollments;
delete from courses;
delete from students;

drop table enrollments;
drop table courses;
drop table students;

-- Teachers
INSERT INTO teachers (full_name) VALUES
                                     ('Dr. Newton'), ('Dr. Herodotus'), ('Dr. Einstein');

-- Students
INSERT INTO students (full_name, email) VALUES
                                            ('Alice Johnson', 'alice@example.com'),
                                            ('Bob Smith', 'bob@example.com'),
                                            ('Carol White', 'carol@example.com');

-- Courses
INSERT INTO courses (course_name, description, teacher_id) VALUES
                                                               ('Math 101', 'Intro to Algebra and Calculus', 1),
                                                               ('History 201', 'World History Overview', 2),
                                                               ('Physics 301', 'Mechanics and Thermodynamics', 3);

-- Enrollments
INSERT INTO enrollments (student_id, course_id, enrollment_date, grade) VALUES
                                                                            (1, 1, '2025-01-10', 'A'),
                                                                            (1, 2, '2025-01-15', 'B+'),
                                                                            (2, 1, '2025-01-11', 'B'),
                                                                            (3, 3, '2025-01-12', 'A-');




-- Denormalized:

CREATE TABLE enrollments_denorm (
                                    id SERIAL PRIMARY KEY,

    -- student info (repeated for each course)
                                    student_id INT,
                                    student_name VARCHAR(100),
                                    student_email VARCHAR(100),

    -- course info (repeated for each student)
                                    course_id INT,
                                    course_name VARCHAR(100),
                                    course_description TEXT,

    -- teacher info (repeated for each course-student pair)
                                    teacher_name VARCHAR(100),

    -- enrollment info
                                    enrollment_date DATE,
                                    grade VARCHAR(2)  -- e.g., A, B+, etc.
);


INSERT INTO enrollments_denorm (
    student_id, student_name, student_email,
    course_id, course_name, course_description,
    teacher_name, enrollment_date, grade
) VALUES
      (1, 'Alice Johnson', 'alice@example.com',
       1, 'Math 101', 'Intro to Algebra and Calculus',
       'Dr. Newton', '2025-01-10', 'A'),

      (1, 'Alice Johnson', 'alice@example.com',
       2, 'History 201', 'World History Overview',
       'Dr. Herodotus', '2025-01-15', 'B+'),

      (2, 'Bob Smith', 'bob@example.com',
       1, 'Math 101', 'Intro to Algebra and Calculus',
       'Dr. Newton', '2025-01-11', 'B'),

      (3, 'Carol White', 'carol@example.com',
       3, 'Physics 301', 'Mechanics and Thermodynamics',
       'Dr. Einstein', '2025-01-12', 'A-');

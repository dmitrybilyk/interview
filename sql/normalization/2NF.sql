-- 1. Create the Students table
CREATE TABLE students (
                          student_id SERIAL PRIMARY KEY,
                          name VARCHAR(100),
                          email VARCHAR(100) UNIQUE
);

-- 2. Create the Courses table
-- (Notice we still have teacher info here for now)
CREATE TABLE courses (
                         course_id SERIAL PRIMARY KEY,
                         course_name VARCHAR(100),
                         teacher_name VARCHAR(100),
                         teacher_email VARCHAR(100)
);

-- 3. Create the Link table (Enrollments)
CREATE TABLE enrollments (
                             enrollment_id SERIAL PRIMARY KEY,
                             student_id INT REFERENCES students(student_id),
                             course_id INT REFERENCES courses(course_id),
                             enrollment_date DATE
);
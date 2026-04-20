-- 1. Create the Teachers table
CREATE TABLE teachers (
                          teacher_id SERIAL PRIMARY KEY,
                          name VARCHAR(100),
                          email VARCHAR(100) UNIQUE
);

-- 2. Modify (or Recreate) the Courses table
-- We drop the old one to fix the structure
DROP TABLE IF EXISTS enrollments; -- Drop child first due to FK constraints
DROP TABLE IF EXISTS courses;

CREATE TABLE courses (
                         course_id SERIAL PRIMARY KEY,
                         course_name VARCHAR(100) NOT NULL,
                         teacher_id INT REFERENCES teachers(teacher_id)
);

-- 3. Re-create Enrollments to link Students and Courses
CREATE TABLE enrollments (
                             enrollment_id SERIAL PRIMARY KEY,
                             student_id INT REFERENCES students(student_id),
                             course_id INT REFERENCES courses(course_id),
                             enrollment_date DATE DEFAULT CURRENT_DATE
);
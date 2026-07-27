-- ==========================================
-- 1. CLEANUP (Drop existing tables)
-- ==========================================
-- We use CASCADE to handle foreign key dependencies automatically
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS course_teachers CASCADE;
DROP TABLE IF EXISTS materials CASCADE;
DROP TABLE IF EXISTS course_versions CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;

-- ==========================================
-- 2. CORE ENTITIES (Master Data)
-- ==========================================

CREATE TABLE students (
                          student_id SERIAL PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teachers (
                          teacher_id SERIAL PRIMARY KEY,
                          full_name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          bio TEXT
);

-- ==========================================
-- 3. COURSE STRUCTURE (Versioning Design)
-- ==========================================

-- The "Container" for the course
CREATE TABLE courses (
                         course_id SERIAL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         slug VARCHAR(100) UNIQUE NOT NULL -- for clean URLs
);

-- The specific snapshot of content
CREATE TABLE course_versions (
                                 version_id SERIAL PRIMARY KEY,
                                 course_id INT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
                                 version_name VARCHAR(50) NOT NULL, -- e.g., 'Spring 2026'
                                 is_published BOOLEAN DEFAULT false,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 4. RELATIONSHIPS & CONTENT
-- ==========================================

-- Many-to-Many: Multiple teachers can be assigned to a specific version
CREATE TABLE course_teachers (
                                 version_id INT REFERENCES course_versions(version_id) ON DELETE CASCADE,
                                 teacher_id INT REFERENCES teachers(teacher_id) ON DELETE CASCADE,
                                 PRIMARY KEY (version_id, teacher_id)
);

-- One-to-Many: A version has many materials
CREATE TABLE materials (
                           material_id SERIAL PRIMARY KEY,
                           version_id INT NOT NULL REFERENCES course_versions(version_id) ON DELETE CASCADE,
                           title VARCHAR(255) NOT NULL,
                           url TEXT,
                           material_type VARCHAR(20) CHECK (material_type IN ('VIDEO', 'PDF', 'QUIZ'))
);

-- Many-to-Many: Students enroll in a specific VERSION of a course
CREATE TABLE enrollments (
                             enrollment_id SERIAL PRIMARY KEY,
                             student_id INT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
                             version_id INT NOT NULL REFERENCES course_versions(version_id) ON DELETE CASCADE,
                             enrollment_date DATE DEFAULT CURRENT_DATE,
                             grade DECIMAL(5,2),
                             UNIQUE(student_id, version_id) -- Prevent double enrollment in the same version
);

-- ==========================================
-- 5. PERFORMANCE (Indexes)
-- ==========================================
-- Always index foreign keys used in frequent JOINs
CREATE INDEX idx_course_versions_course_id ON course_versions(course_id);
CREATE INDEX idx_materials_version_id ON materials(version_id);
CREATE INDEX idx_enrollments_student_id ON enrollments(student_id);
CREATE INDEX idx_enrollments_version_id ON enrollments(version_id);
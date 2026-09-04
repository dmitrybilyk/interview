-- 1. Check constraints

ALTER TABLE enrollments
    ADD CONSTRAINT check_valid_grade
        CHECK (grade >= 0 AND grade <= 100);

ALTER TABLE materials
    ADD CONSTRAINT check_material_type
        CHECK (material_type IN ('VIDEO', 'PDF', 'QUIZ', 'LAB'));

-- 2. Unique Constraints (Multi-Column)

-- Can be on column - email VARCHAR(100) UNIQUE NOT NULL,
-- Also can be logical:
ALTER TABLE enrollments
    ADD CONSTRAINT unique_student_version
        UNIQUE (student_id, version_id);

-- 3. Unique index

CREATE UNIQUE INDEX idx_one_active_version_per_course
    ON course_versions (course_id)
    WHERE (is_published = true);
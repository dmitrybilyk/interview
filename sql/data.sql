DO $$
BEGIN
    -- 1. Insert Teachers
    INSERT INTO teachers (full_name, email, bio)
    SELECT
        'Teacher ' || i,
        'teacher' || i || '@university.com',
        'Expert in subject ' || i
    FROM generate_series(1, 50) AS i;

    -- 2. Insert Students
    INSERT INTO students (full_name, email)
    SELECT
        'Student Name ' || i,
        'student' || i || '@gmail.com'
    FROM generate_series(1, 1000) AS i;

    -- 3. Insert Courses & Versions
    FOR i IN 1..10 LOOP
        INSERT INTO courses (title, slug)
        VALUES ('Course ' || i, 'course-' || i)
        RETURNING course_id INTO i; -- Reuse the ID for versions

        INSERT INTO course_versions (course_id, version_name, is_published)
        VALUES (i, 'v1.0', true), (i, 'v2.0', true);
    END LOOP;

    -- 4. Assign Teachers to Versions (Many-to-Many)
    -- This assigns 2 random teachers to every version
    INSERT INTO course_teachers (version_id, teacher_id)
    SELECT v.version_id, t.teacher_id
    FROM course_versions v
    CROSS JOIN LATERAL (
        SELECT teacher_id FROM teachers ORDER BY random() LIMIT 2
    ) t;

    -- 5. Create 20,000 Random Enrollments
    INSERT INTO enrollments (student_id, version_id, enrollment_date, grade)
    SELECT
        floor(random() * 1000 + 1)::int,     -- Random student 1-1000
        floor(random() * 20 + 1)::int,       -- Random version 1-20
        CURRENT_DATE - (random() * 365)::int, -- Random date in last year
        (random() * 100)::decimal(5,2)
    FROM generate_series(1, 20000)
    ON CONFLICT (student_id, version_id) DO NOTHING; -- Skip duplicates

END $$;
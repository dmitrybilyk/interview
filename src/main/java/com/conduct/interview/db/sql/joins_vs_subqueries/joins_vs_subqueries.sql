EXPLAIN ANALYZE
SELECT s.full_name, s.email
FROM students s
         JOIN enrollments e ON s.student_id = e.student_id
WHERE e.version_id = 5;



EXPLAIN ANALYZE
SELECT full_name, email
FROM students
WHERE student_id IN (
    SELECT student_id
    FROM enrollments
    WHERE version_id = 5
);

-- 1. Correlated query (loop trap):

Select s.full_name from students s
where 50 > (Select count(*)
            from enrollments e
            where e.student_id = s.student_id);
CREATE TABLE departments (
                             id INT PRIMARY KEY,
                             name TEXT
);

CREATE TABLE employees (
                           id INT PRIMARY KEY,
                           name TEXT,
                           salary INT,
                           department_id INT REFERENCES departments(id)
);

INSERT INTO departments (id, name) VALUES
                                       (1, 'Engineering'), (2, 'HR'), (3, 'Marketing');

INSERT INTO employees (id, name, salary, department_id) VALUES
                                                            (1, 'Alice', 8000, 1),
                                                            (2, 'Bob', 9000, 1),
                                                            (3, 'Charlie', 3000, 2),
                                                            (4, 'David', 3200, 2),
                                                            (5, 'Eve', 4000, 3);

ALTER TABLE employees
    ADD COLUMN manager_id INT;

UPDATE employees SET manager_id = NULL WHERE id IN (1, 2);

UPDATE employees SET manager_id = 2 WHERE id IN (3, 4);

UPDATE employees SET manager_id = 1 WHERE id = 5;


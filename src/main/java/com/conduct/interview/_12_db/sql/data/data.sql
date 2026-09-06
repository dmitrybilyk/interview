-- Customers and orders

CREATE TABLE customers (
                           customer_id SERIAL PRIMARY KEY,
                           name VARCHAR(100),
                           city VARCHAR(50)
);

CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        customer_id INT REFERENCES customers(customer_id),
                        order_date DATE,
                        total_amount DECIMAL(10,2)
);

INSERT INTO customers (name, city) VALUES
                                       ('Alice', 'New York'), ('Bob', 'London'), ('Charlie', 'New York'), ('David', 'Berlin');

INSERT INTO orders (customer_id, order_date, total_amount) VALUES
                                                               (1, '2023-01-01', 150.00),
                                                               (1, '2023-02-01', 200.00),
                                                               (2, '2023-01-15', 50.00),
                                                               (3, '2023-03-01', 300.00),
                                                               (1, '2023-04-01', 50.00);


-- employes
CREATE TABLE workers (
                           employee_id INT PRIMARY KEY,
                           name VARCHAR(100),
                           job_title VARCHAR(100),
                           manager_id INT -- This points back to employee_id in the same table
);

INSERT INTO workers (employee_id, name, job_title, manager_id) VALUES
                                                                     (1, 'Alice', 'CEO', NULL),
                                                                     (2, 'Bob', 'VP of Engineering', 1),
                                                                     (3, 'Charlie', 'VP of Sales', 1),
                                                                     (4, 'David', 'Engineering Manager', 2),
                                                                     (5, 'Eve', 'Senior Developer', 4),
                                                                     (6, 'Frank', 'Junior Developer', 4),
                                                                     (7, 'Grace', 'Sales Rep', 3);


CREATE TABLE earnings (
                       sale_id INT PRIMARY KEY,
                       worker_id INT,
                       amount DECIMAL(10,2),
                       sale_date DATE
);

INSERT INTO earnings (sale_id, worker_id, amount, sale_date) VALUES
                                                              (1, 5, 5000, '2026-01-10'),
                                                              (2, 5, 3000, '2026-02-15'),
                                                              (3, 6, 1500, '2026-01-20'),
                                                              (4, 7, 9000, '2026-03-01'),
                                                              (5, 7, 2000, '2026-03-10');
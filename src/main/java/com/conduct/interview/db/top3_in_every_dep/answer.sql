SELECT *
FROM (
         SELECT e.*,
                DENSE_RANK() OVER (PARTITION BY e.department_id ORDER BY e.salary DESC) AS rank_in_dept
         FROM employees e
     ) ranked
WHERE rank_in_dept = 2;


SELECT e.*,
       DENSE_RANK() OVER (PARTITION BY e.department_id ORDER BY e.salary DESC) AS rank_in_dept
FROM employees e



select * from (
                  select e.*, dense_rank() over (partition by e.department_id order by e.salary desc) as dep_rank from employees e) ranked
where dep_rank = 1;

select * from
(select e.*, dense_rank() over (partition by e.department_id order by salary desc ) as dep_rank from employees e) ranked
where dep_rank = 1;
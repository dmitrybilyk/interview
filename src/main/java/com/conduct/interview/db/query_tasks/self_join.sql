select w.name, m.name from workers w
left join workers m on w.manager_id = m.employee_id;

select w.name AS worker_name,
       m.name AS manager_name,
       gm.name AS grand_manager_name from workers w
                               join workers m on w.manager_id = m.employee_id
                               join workers gm on gm.manager_id = m.employee_id
where gm.name = 'Bob';


SELECT
    w.name AS worker_name,
    m.name AS manager_name,
    gm.name AS grand_manager_name
FROM workers w
         JOIN workers m ON w.manager_id = m.employee_id      -- Link worker to their boss
         JOIN workers gm ON m.manager_id = gm.employee_id   -- Link that boss to their boss
WHERE gm.name = 'Bob';

select w.name, m.name from workers w
                               left join workers m on w.manager_id = m.employee_id
where m.employee_id is null ;


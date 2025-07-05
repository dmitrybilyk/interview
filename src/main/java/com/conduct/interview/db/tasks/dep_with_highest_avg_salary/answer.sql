select d.id, d.name, avg(e.salary) from departments d join employees e on d.id = e.department_id
                                   group by d.id order by avg(e.salary) desc;

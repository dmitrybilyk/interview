select *
from employees emp
         join (select d.id, d.name, avg(e.salary) as avg_sal
               from departments d
                        join employees e on d.id = e.department_id
               group by d.id
               order by avg(e.salary) desc) avrg on emp.department_id = avrg.id
where emp.salary > avrg.avg_sal
;




select c.name,
       sum(o.total_amount) as sum,
       case
        when sum(o.total_amount) > 300 then 'VIP'
        when sum(o.total_amount) > 100 then 'REGULAR'
        else 'BUDGET'
       end
from customers c
    join public.orders o on c.customer_id = o.customer_id
group by c.customer_id
order by sum desc;
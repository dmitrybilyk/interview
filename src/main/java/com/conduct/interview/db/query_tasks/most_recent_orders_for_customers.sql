with ranked_orders as (
    select c.name, o.order_date, o.total_amount,
           row_number() over (
               partition by c.customer_id order by total_amount
               ) as rn
    from customers c
             join public.orders o on c.customer_id = o.customer_id
)
select * from ranked_orders where rn = 1;
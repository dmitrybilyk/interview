WITH WorkerTotals AS (
    -- Step 1: Calculate total for each worker
    SELECT
        worker_id,
        SUM(amount) AS total_sales
    FROM earnings
    GROUP BY worker_id
),
     CompanyAvg AS (
         -- Step 2: Get the average of those totals
         SELECT AVG(total_sales) AS avg_amount FROM WorkerTotals
     )
-- Step 3: Final Output
SELECT
    w.name,
    wt.total_sales
FROM workers w
         JOIN WorkerTotals wt ON w.employee_id = wt.worker_id
         CROSS JOIN CompanyAvg ca
WHERE wt.total_sales > ca.avg_amount;
-- Query 1 weekly sales history SQ1
SELECT DATE_TRUNC('week', time) AS week, COUNT(*) AS order_count
FROM transaction
GROUP BY week
ORDER BY week;

-- Query 2. realistic sales SQ2
SELECT EXTRACT(HOUR FROM time) AS hour, COUNT(*) AS order_count, SUM(price) AS total_sales
FROM transaction
GROUP BY hour
ORDER BY hour;

-- Query 3. peak sales day SQ3
SELECT DATE(time) AS day, SUM(price) AS total_sales
FROM transaction
GROUP BY day
ORDER BY total_sales DESC
LIMIT 10;

-- Query 4. menu item inventory SQ4
SELECT product.name, product.inventory AS inventory_count
FROM product
ORDER BY inventory_count DESC;

-- Query 5. top products by sales
SELECT product.name AS top_seller, SUM(transaction_item.subtotal) AS sales
FROM product
JOIN transaction_item ON transaction_item.product_id = product.id
GROUP BY product.name
ORDER BY sales DESC
LIMIT 10;

-- Query 6. payment type. total sales
SELECT payment_type, COUNT(*) AS transaction_count, SUM(price) AS total_sales
FROM transaction
GROUP BY payment_type;

-- Query 7. top products by total sold
SELECT product.name, COUNT(transaction_item.product_id) AS total_sold
FROM transaction_item
JOIN product ON transaction_item.product_id = product.id
GROUP BY product.name
ORDER BY total_sold DESC
LIMIT 10;

-- Query 8. customers with most transactions
SELECT customer_id,COUNT(*) AS transaction_count
FROM transaction
WHERE customer_id IS NOT NULL
GROUP BY customer_id
ORDER BY transaction_count DESC
LIMIT 1;

-- Query 9. average order cost
SELECT AVG(price) AS avg_order_value
FROM transaction;

-- Query 10. best employee by transaction count
SELECT employee_id, employee.name as employee_name, COUNT(*) AS transaction_count
FROM transaction
JOIN employee ON transaction.employee_id = employee.id
GROUP BY employee_id, employee_name
ORDER BY transaction_count DESC
LIMIT 1;

-- Query 11. peak hour
SELECT EXTRACT(HOUR FROM time) AS hour, COUNT(*) AS order_count
FROM transaction
GROUP BY hour
ORDER BY order_count DESC
LIMIT 1;

-- Query 12. low stock items
SELECT name, inventory
FROM supply
WHERE inventory < 50
ORDER BY inventory ASC;

-- Query 13. total revenue product category
SELECT product_type, SUM(transaction_item.subtotal) AS total_revenue
FROM transaction_item
JOIN product ON transaction_item.product_id = product.id
GROUP BY product_type
ORDER BY total_revenue DESC;

-- Query 14. sales trends by week
SELECT DATE_TRUNC('week', time) AS week, SUM(price) AS total_sales
FROM transaction
GROUP BY week
ORDER BY week;

-- Query 15. frequent customers
SELECT customer_id, COUNT(*) AS order_count
FROM transaction
WHERE customer_id IS NOT NULL
GROUP BY customer_id
ORDER BY order_count DESC
LIMIT 5;

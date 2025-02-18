#!/bin/bash

./run-sql.sh ./gen-sql/delete-all.sql \
    ./gen-sql/generate-products.sql \
    ./gen-sql/generate-employees.sql \
    ./gen-sql/generate-customers.sql \
    ./gen-sql/generate-transactions.sql \
    ./queries.sql

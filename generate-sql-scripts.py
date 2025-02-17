#!/bin/python3

import hashlib
import os
import random as r
from datetime import datetime, timedelta

SQL_DIR = "gen-sql"

customer_table_name = "customer"
employee_table_name = "employee"
product_table_name = "product"
transaction_table_name = "transaction"
transaction_item_table_name = "transaction_item"
supply_table_name = "supply"


os.makedirs(SQL_DIR, exist_ok=True)


def flip_coin(p=0.5):
    return r.random() < p


generate_customers_script_name = "generate-customers.sql" # test


def generate_phonenumber(l=10):
    return "".join([str(r.randint(0, 9)) for _ in range(l)])


customer_count = 1_000

with open(os.path.join(SQL_DIR, generate_customers_script_name), "w") as file:
    file.write(f"INSERT INTO {customer_table_name} (phone_number) VALUES\n\t")

    file.write(
        ",\n\t".join([f"('{generate_phonenumber()}')" for _ in range(customer_count)])
    )

    file.write(";")


generate_employees_script_name = "generate-employees.sql"


def generate_password_hash(pw):
    return hashlib.sha256(pw.encode()).hexdigest()


employee_names = [
    "Macsen Casaus",
    "Luke Conran",
    "Surada Suwansathit",
    "Kamryn Vogel",
    "Christian Fadal",
]

# everybody's password is "password"
password_hash = generate_password_hash("password")

with open(os.path.join(SQL_DIR, generate_employees_script_name), "w") as file:
    file.write(f"INSERT INTO {employee_table_name} (name, password, admin) VALUES\n\t")

    file.write(
        ",\n\t".join(
            [f"('{employee}', '{password_hash}', TRUE)" for employee in employee_names]
        )
    )

    file.write(";")

generate_products_script_name = "generate-products.sql"

# TODO: maybe make these dicts or their own (data)classes for __str__
products = [
    ("milk_tea", "Classic Milk Tea", 500, 100),
    ("milk_tea", "Okinawa Milk Tea", 550, 80),
    ("fruit_tea", "Mango Fruit Tea", 450, 90),
    ("fruit_tea", "Strawberry Fruit Tea", 470, 85),
    ("brewed_tea", "Earl Grey Tea", 300, 120),
    ("brewed_tea", "Jasmine Green Tea", 320, 110),
    ("fresh_milk", "Fresh Taro Milk", 600, 75),
    ("fresh_milk", "Matcha Fresh Milk", 620, 70),
    ("ice_blended", "Mocha Ice Blended", 650, 60),
    ("ice_blended", "Caramel Ice Blended", 670, 55),
    ("tea_mojito", "Lemon Tea Mojito", 550, 50),
    ("tea_mojito", "Passionfruit Tea Mojito", 580, 45),
    ("creama", "Oolong Crema", 520, 65),
    ("creama", "Black Tea Crema", 530, 60),
    ("ice_cream", "Vanilla Ice Cream", 400, 150),
    ("ice_cream", "Chocolate Ice Cream", 420, 140),
    ("misc", "Bottled Water", 200, 200),
    ("misc", "Canned Soda", 250, 180),
    ("topping", "Pearl Topping", 50, 500),
    ("topping", "Aloe Vera Topping", 60, 450),
    ("special_item", "Limited Edition Tea", 700, 30),
]

with open(os.path.join(SQL_DIR, generate_products_script_name), "w") as file:
    file.write(
        f"INSERT INTO {product_table_name} (product_type, name, price, inventory) VALUES\n\t"
    )
    file.write(",\n\t".join(map(str, products)))
    file.write(";")

generate_transactions_script_name = "generate-transactions.sql"


payment_types = [
    "cash",
    "card",
    "gift_card",
    "check",
]


def generate_cc_digits():
    return "".join([str(r.randint(0, 9)) for _ in range(4)])


def wrap_quotes(s):
    return f"'{s}'"


def generate_datetime(year_from="2020"):
    start = datetime.strptime(f"1/1/{year_from}", "%m/%d/%Y")
    delta = datetime.now() - start
    int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
    rand_second = r.randrange(int_delta)
    return start + timedelta(seconds=rand_second)


# TODO: maybe make this a different distribution
def generate_tip(lower=0, upper=500):
    return r.randint(lower, upper)


transaction_count = 10_000

with open(os.path.join(SQL_DIR, generate_transactions_script_name), "w") as file:
    file.write(
        f"INSERT INTO {transaction_table_name} "
        f"(payment_type, "
        f"cc_digits, "
        f"time, "
        f"price, "
        f"tip, "
        f"customer_id) "
        f"VALUES\n\t"
    )

    values = []
    products_bought = []

    for transaction_id in range(1, transaction_count + 1):
        use_customer_id = flip_coin(0.75)

        # TODO: make credit card more common payment type
        payment_type = payment_types[r.randint(0, 3)]

        product_count = r.randint(1, 5)

        subtotal = 0
        total = 0

        for _ in range(product_count):
            product_id = r.randint(1, len(products))
            quantity = r.randint(1, 3)
            subtotal = products[product_id - 1][2] * quantity
            products_bought.append(
                {
                    "transaction_id": transaction_id,
                    "product_id": product_id,
                    "quantity": quantity,
                    "subtotal": subtotal,
                }
            )
            total += subtotal

        values.append(
            f"("
            f"'{payment_type}', "
            f"{(wrap_quotes(generate_cc_digits()) + ', ') if payment_type == 'card' else 'NULL, '}"
            f"'{generate_datetime()}', "
            f"{total}, "
            f"{generate_tip()}, "
            f"{str(r.randint(1, customer_count)) if use_customer_id else 'NULL'}"
            f")"
        )

    file.write(",\n\t".join(values))
    file.write(";\n\n")

    file.write(
        f"INSERT INTO {transaction_item_table_name} "
        "(transaction_id, product_id, quantity, subtotal) VALUES\n\t"
    )

    file.write(
        ",\n\t".join(
            [
                f"({p['transaction_id']}, {p['product_id']}, {p['quantity']}, {p['subtotal']})"
                for p in products_bought
            ]
        )
    )
    file.write(";")


delete_all_script_name = "delete-all.sql"

# NOTE: order matters
table_names = [
    transaction_item_table_name,
    transaction_table_name,
    customer_table_name,
    employee_table_name,
    product_table_name,
    supply_table_name,
]

with open(os.path.join(SQL_DIR, delete_all_script_name), "w") as file:
    for table in table_names:
        file.write(f"DELETE FROM {table};\n")

    file.write("\n")

    for table in table_names:
        file.write(f"ALTER SEQUENCE {table}_id_seq RESTART WITH 1;\n")

CREATE TABLE Customer (
    id              SERIAL NOT NULL,
    phone_number    VARCHAR(32) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TYPE payment_type AS ENUM('cash', 'card', 'gift_card', 'check');

CREATE TABLE Transaction (
    id              SERIAL NOT NULL,
    payment_type    payment_type NOT NULL,
    cc_digits       VARCHAR(4),
    time            TIMESTAMP NOT NULL,
    price           INT NOT NULL,
    tip             INT NOT NULL,
    customer_id     INT,
    employee_id     INT NOT NULL,

    PRIMARY KEY (id),

    FOREIGN KEY (customer_id) REFERENCES Customer(id),

    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);

CREATE TYPE product_type AS ENUM (
    'milk_tea',
    'fruit_tea',
    'brewed_tea',
    'fresh_milk',
    'ice_blended',
    'tea_mojito',
    'creama',
    'ice_cream',
    'misc',
    'topping',
    'special_item'
);

CREATE TABLE Product (
    id              SERIAL NOT NULL,
    product_type    product_type NOT NULL,
    name            VARCHAR(256) NOT NULL,
    price           INT NOT NULL,
    inventory       INT NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE Transaction_Item (
    id              SERIAL NOT NULL,
    transaction_id  INT NOT NULL,
    product_id      INT NOT NULL,
    quantity        INT NOT NULL,
    subtotal        INT NOT NULL,

    PRIMARY KEY (id),

    FOREIGN KEY (transaction_id) REFERENCES Transaction(id),

    FOREIGN KEY (product_id) REFERENCES Product(id)
);

CREATE TABLE Employee (
    id          SERIAL NOT NULL,
    name        VARCHAR(256) NOT NULL,
    password    VARCHAR(256) NOT NULL,
    admin       BOOL NOT NULL,

    PRIMARY KEY (id)
);

CREATE TYPE supply_type AS ENUM (
    'cup',
    'straw',
    'paper'
);

CREATE TABLE Supply (
    id          SERIAL NOT NULL,
    name        VARCHAR(256) NOT NULL,
    supply_type supply_type NOT NULL,
    inventory   INT NOT NULL,

    PRIMARY KEY (id)
);

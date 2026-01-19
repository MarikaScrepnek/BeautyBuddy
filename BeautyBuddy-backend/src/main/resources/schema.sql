CREATE TABLE IF NOT EXISTS brand (
    brand_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS category (
    category_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    parent_category_id INT
);

CREATE TABLE IF NOT EXISTS product (
    product_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    brand_id INT REFERENCES brand(brand_id) NOT NULL,
    category_id INT REFERENCES category(category_id) NOT NULL,
    price NUMERIC(5, 2),
    image_link TEXT,
    product_link TEXT,
    description TEXT,
    rating NUMERIC(3, 2),
    CONSTRAINT unique_product_name_brand UNIQUE(name, brand_id)
);

CREATE TABLE IF NOT EXISTS ingredient (
    ingredient_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    normalized_name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS product_ingredient (
    product_id INT REFERENCES product(product_id),
    ingredient_id INT REFERENCES ingredient(ingredient_id),
    position INT,
    PRIMARY KEY (product_id, ingredient_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    date_joined TIMESTAMP DEFAULT NOW()
);

CREATE EXTENSION IF NOT EXISTS unaccent;
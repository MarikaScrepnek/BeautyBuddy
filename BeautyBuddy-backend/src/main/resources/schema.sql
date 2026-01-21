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
    rating NUMERIC(3, 2),
    raw_ingredients TEXT,
    may_contain_raw_ingredients TEXT,
    CONSTRAINT unique_product_name_brand UNIQUE(name, brand_id)
);

CREATE TABLE IF NOT EXISTS ingredient (
    ingredient_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    canonical_id INT REFERENCES ingredient(ingredient_id)
);

CREATE TABLE IF NOT EXISTS product_ingredient (
    product_ingredient_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    ingredient_id INT REFERENCES ingredient(ingredient_id) ON DELETE CASCADE,
    position INT,
    UNIQUE (product_id, ingredient_id)
);

CREATE TABLE IF NOT EXISTS may_contain_ingredient (
    may_contain_ingredient_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    ingredient_id INT REFERENCES ingredient(ingredient_id) ON DELETE CASCADE,
    UNIQUE (product_id, ingredient_id)
);

CREATE TABLE IF NOT EXISTS product_shade (
    product_shade_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    shade_name TEXT NOT NULL,
    shade_hex_code TEXT,
    image_link TEXT,
    product_link TEXT,
    UNIQUE (product_id, shade_name)
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    date_joined TIMESTAMP DEFAULT NOW()
);

CREATE EXTENSION IF NOT EXISTS unaccent;
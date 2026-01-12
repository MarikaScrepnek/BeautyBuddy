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
    brand_id INT REFERENCES brand(brand_id),
    category_id INT REFERENCES category(category_id),
    price NUMERIC(5, 2),
    image_link TEXT,
    product_link TEXT,
    description TEXT,
    rating NUMERIC(3, 2),
    CONSTRAINT unique_product_name_brand UNIQUE(name, brand_id)
);
CREATE TABLE brand (
    brand_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE category (
    category_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    parent_category_id INT
);

CREATE TABLE product (
    product_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    brand_id INT REFERENCES brand(brand_id),
    category_id INT REFERENCES category(category_id),
    price NUMERIC(5, 2),
    image_link TEXT,
    product_link TEXT,
    description TEXT,
    rating NUMERIC(3, 2)
);
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
    shade_number INT,
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

CREATE TABLE IF NOT EXISTS wishlist (
    wishlist_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id)
);

CREATE TABLE IF NOT EXISTS wishlist_item (
    wishlist_item_id SERIAL PRIMARY KEY,
    wishlist_id INT REFERENCES wishlist(wishlist_id) ON DELETE CASCADE,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    shade_id INT REFERENCES product_shade(product_shade_id) ON DELETE SET NULL,
    added_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (wishlist_id, product_id, shade_id)
);

CREATE TABLE IF NOT EXISTS review (
    review_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    product_shade_id INT REFERENCES product_shade(product_shade_id) ON DELETE SET NULL,
    rating NUMERIC(3, 2) CHECK (rating >= 0 AND rating <= 5),
    review_text TEXT,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE,
    UNIQUE (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS review_image (
    review_image_id SERIAL PRIMARY KEY,
    review_id INT REFERENCES review(review_id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS review_helpful_vote (
    review_helpful_vote_id SERIAL PRIMARY KEY,
    review_id INT REFERENCES review(review_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS question (
    question_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    answered BOOLEAN DEFAULT FALSE,
    upvote_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS question_upvote (
    question_upvote_id SERIAL PRIMARY KEY,
    question_id INT REFERENCES question(question_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (question_id, user_id)
);

CREATE TABLE IF NOT EXISTS answer (
    answer_id SERIAL PRIMARY KEY,
    question_id INT REFERENCES question(question_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    answer_text TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS answer_helpful_vote (
    answer_helpful_vote_id SERIAL PRIMARY KEY,
    answer_id INT REFERENCES answer(answer_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (answer_id, user_id)
);

CREATE TABLE IF NOT EXISTS review_report (
    review_report_id SERIAL PRIMARY KEY,
    review_id INT REFERENCES review(review_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS question_report (
    question_report_id SERIAL PRIMARY KEY,
    question_id INT REFERENCES question(question_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (question_id, user_id)
);

CREATE TABLE IF NOT EXISTS answer_report (
    answer_report_id SERIAL PRIMARY KEY,
    answer_id INT REFERENCES answer(answer_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (answer_id, user_id)
);

CREATE EXTENSION IF NOT EXISTS unaccent;
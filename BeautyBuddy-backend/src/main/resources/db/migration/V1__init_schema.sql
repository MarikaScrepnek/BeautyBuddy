CREATE EXTENSION unaccent;

CREATE TYPE target_type_enum AS ENUM ('review', 'question', 'answer', 'discussion', 'discussion_answer');

CREATE TABLE brand (
    brand_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE category (
    category_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    parent_category_id INT REFERENCES category(category_id) DEFAULT NULL
);

CREATE TABLE product (
    product_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    brand_id INT REFERENCES brand(brand_id) NOT NULL,
    category_id INT REFERENCES category(category_id) NOT NULL,
    price NUMERIC(10, 2),
    image_link TEXT,
    product_link TEXT,
    rating NUMERIC(2, 1),
    raw_ingredients TEXT,
    may_contain_raw_ingredients TEXT,
    CONSTRAINT unique_product_name_brand UNIQUE(name, brand_id)
);

CREATE TABLE ingredient (
    ingredient_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    canonical_id INT REFERENCES ingredient(ingredient_id)
);

CREATE TABLE product_ingredient (
    product_ingredient_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    ingredient_id INT REFERENCES ingredient(ingredient_id) ON DELETE CASCADE,
    position INT,
    UNIQUE (product_id, ingredient_id)
);

CREATE TABLE may_contain_ingredient (
    may_contain_ingredient_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    ingredient_id INT REFERENCES ingredient(ingredient_id) ON DELETE CASCADE,
    UNIQUE (product_id, ingredient_id)
);

CREATE TABLE product_shade (
    product_shade_id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    shade_name TEXT NOT NULL,
    shade_hex_code TEXT,
    shade_number INT,
    image_link TEXT,
    product_link TEXT,
    UNIQUE (product_id, shade_name)
);

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    date_joined TIMESTAMP DEFAULT NOW(),
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    unread_notifications_count INT DEFAULT 0
);
CREATE UNIQUE INDEX idx_users_lower_email ON users ((lower(email)));

CREATE TABLE wishlist (
    wishlist_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id)
);

CREATE TABLE wishlist_item (
    wishlist_item_id SERIAL PRIMARY KEY,
    wishlist_id INT REFERENCES wishlist(wishlist_id) ON DELETE CASCADE,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    shade_id INT REFERENCES product_shade(product_shade_id) ON DELETE SET NULL,
    added_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (wishlist_id, product_id, shade_id)
);

CREATE TABLE review (
    review_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    product_shade_id INT REFERENCES product_shade(product_shade_id) ON DELETE SET NULL,
    rating NUMERIC(2, 1) CHECK (rating >= 0 AND rating <= 5),
    review_text TEXT,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE,
    UNIQUE (user_id, product_id)
);

CREATE TABLE review_image (
    review_image_id SERIAL PRIMARY KEY,
    review_id INT REFERENCES review(review_id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE question (
    question_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
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

CREATE TABLE answer (
    answer_id SERIAL PRIMARY KEY,
    question_id INT REFERENCES question(question_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    answer_text TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE discussion (
    discussion_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE discussion_answer (
    discussion_answer_id SERIAL PRIMARY KEY,
    discussion_id INT REFERENCES discussion(discussion_id) ON DELETE CASCADE,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    parent_discussion_answer_id INT REFERENCES discussion_answer(discussion_answer_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_discussion_pin (
  user_discussion_pin_id SERIAL PRIMARY KEY,
  discussion_id INT REFERENCES discussion(discussion_id) ON DELETE CASCADE,
  user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
  pinned_at TIMESTAMP DEFAULT NOW(),
  UNIQUE (discussion_id, user_id)
);

CREATE TABLE user_follow (
    user_follow_id SERIAL PRIMARY KEY,
    follower_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    following_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    followed_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (follower_id, following_id),
    CHECK (follower_id <> following_id)
);

CREATE TABLE notifications (
  notification_id SERIAL PRIMARY KEY,
  recipient_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
  actor_id INT NULL REFERENCES users(user_id),
  type TEXT NOT NULL,            -- e.g. 'review.created','wishlist.added','product.question'
  object_type TEXT,              -- e.g. 'product','review','question'
  object_id INT,                 -- id of the object
  is_read BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE user_notification_pref (
  user_id INT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
  pref JSONB DEFAULT '{}'::jsonb
);

CREATE TABLE public_community_post (
    public_community_post_id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    media JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    deleted_at TIMESTAMP NULL
);

CREATE TABLE upvote (
    upvote_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    target_type target_type_enum NOT NULL,
    target_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE (user_id, target_type, target_id)
);

CREATE TABLE report (
    report_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE CASCADE,
    target_type target_type_enum NOT NULL,
    target_id INT NOT NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    resolved_at TIMESTAMP NULL,
    UNIQUE (user_id, target_type, target_id)
);
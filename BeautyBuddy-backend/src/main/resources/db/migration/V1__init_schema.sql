CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    parent_category_id INT REFERENCES category(id) DEFAULT NULL
);

CREATE TABLE brand (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    brand_id INT NOT NULL REFERENCES brand(id),
    category_id INT NOT NULL REFERENCES category(id),
    price NUMERIC(10, 2),
    image_link TEXT,
    product_link TEXT,
    rating NUMERIC(2, 1),
    raw_ingredients TEXT,
    may_contain_raw_ingredients TEXT,
    UNIQUE(name, brand_id),
    CHECK (rating >= 0 AND rating <= 5),
    CHECK (price >= 0)
);

CREATE TABLE product_shade (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_name TEXT NOT NULL,
    shade_hex_code TEXT,
    shade_number INT,
    image_link TEXT,
    product_link TEXT,

    UNIQUE (product_id, shade_name)
);

CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    canonical_id INT REFERENCES ingredient(id)
);

CREATE TABLE product_ingredient (
    id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(id) ON DELETE CASCADE,
    ingredient_id INT REFERENCES ingredient(id) ON DELETE CASCADE,
    position INT NOT NULL,
    UNIQUE (product_id, ingredient_id)
);

CREATE TABLE may_contain_ingredient (
    id SERIAL PRIMARY KEY,
    product_id INT REFERENCES product(id) ON DELETE CASCADE,
    ingredient_id INT REFERENCES ingredient(id) ON DELETE CASCADE,
    UNIQUE (product_id, ingredient_id)
);

CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email CITEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    date_joined TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    unread_notifications_count INT DEFAULT 0
);

--triggers to update followers_count, following_count, and unread_notifications_count in account table

CREATE TABLE wishlist (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id)
);

CREATE TABLE wishlist_item (
    id SERIAL PRIMARY KEY,
    wishlist_id INT NOT NULL REFERENCES wishlist(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_id INT,
    added_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE (wishlist_id, product_id, shade_id),
    FOREIGN KEY (shade_id, product_id)
        REFERENCES product_shade(id, product_id)
        ON DELETE SET NULL
);

CREATE TYPE routine_category_enum AS ENUM ('skincare', 'makeup', 'haircare', 'bodycare', 'other');

CREATE TABLE routine (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    notes TEXT,
    category routine_category_enum NOT NULL DEFAULT 'other',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, name)
);

CREATE TABLE routine_item (
    id BIGSERIAL PRIMARY KEY,
    routine_id INT NOT NULL REFERENCES routine(id) ON DELETE CASCADE,

    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_id INT,

    step_order INT NOT NULL,
    notes TEXT,

    valid_from TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    valid_to   TIMESTAMPTZ NULL, -- NULL = current version

    created_by INT REFERENCES account(id) ON DELETE SET NULL,

    CHECK (step_order >= 1),
    CHECK (valid_to IS NULL OR valid_to > valid_from),

    FOREIGN KEY (shade_id, product_id)
        REFERENCES product_shade(id, product_id)
        ON DELETE SET NULL
);
CREATE UNIQUE INDEX uniq_current_step_per_routine
  ON routine_item (routine_id, step_order)
  WHERE valid_to IS NULL;
--When someone “removes” an item: you just set valid_to = now() (no special removed_at column needed)
--When they “add” it again later: you insert a new row with a new valid_from

CREATE TABLE routine_image (
    id SERIAL PRIMARY KEY,
    routine_id INT NOT NULL REFERENCES routine(id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

--add routine item schedules later

CREATE TABLE review (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    product_id INT REFERENCES product(id) ON DELETE CASCADE,
    product_shade_id INT,
    rating NUMERIC(2, 1) CHECK (rating >= 0 AND rating <= 5),
    review_text TEXT,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (product_shade_id, product_id)
        REFERENCES product_shade(id, product_id)
        ON DELETE SET NULL
);
-- One active review per account+product+shade (shade chosen)
CREATE UNIQUE INDEX uq_review_account_product_shade_active
ON review (account_id, product_id, product_shade_id)
WHERE deleted_at IS NULL AND product_shade_id IS NOT NULL;
-- One active review per account+product (no shade chosen)
CREATE UNIQUE INDEX uq_review_account_product_noshade_active
ON review (account_id, product_id)
WHERE deleted_at IS NULL AND product_shade_id IS NULL;


CREATE TABLE review_image (
    id SERIAL PRIMARY KEY,
    review_id INT REFERENCES review(id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE question (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    product_id INT REFERENCES product(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    answered BOOLEAN DEFAULT FALSE,
    upvote_count INT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);
--triggers to update 'answered' field in question table when an answer is added or removed

CREATE TABLE answer (
    id SERIAL PRIMARY KEY,
    question_id INT REFERENCES question(id) ON DELETE CASCADE,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    answer_text TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE discussion (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE discussion_answer (
    id SERIAL PRIMARY KEY,
    discussion_id INT REFERENCES discussion(id) ON DELETE CASCADE,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    parent_discussion_answer_id INT REFERENCES discussion_answer(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_discussion_pin (
  id SERIAL PRIMARY KEY,
  discussion_id INT REFERENCES discussion(id) ON DELETE CASCADE,
  account_id INT REFERENCES account(id) ON DELETE CASCADE,
    pinned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (discussion_id, account_id)
);

CREATE TABLE user_follow (
    id SERIAL PRIMARY KEY,
    follower_id INT REFERENCES account(id) ON DELETE CASCADE,
    following_id INT REFERENCES account(id) ON DELETE CASCADE,
    followed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (follower_id, following_id),
    CHECK (follower_id <> following_id)
);

CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
  recipient_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
  actor_id INT NULL REFERENCES account(id) ON DELETE SET NULL,
  type TEXT NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  payload JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE product_question_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);

CREATE TABLE question_answered_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);

CREATE TABLE discussion_answered_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE
);

CREATE TABLE review_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE
);

CREATE TABLE question_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);

CREATE TABLE answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);

CREATE TABLE discussion_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE
);

CREATE TABLE discussion_answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE
);

CREATE TABLE user_notification_pref (
    account_id INT PRIMARY KEY REFERENCES account(id) ON DELETE CASCADE,
    question_on_routine_product BOOLEAN NOT NULL DEFAULT TRUE,
    answer_on_your_question BOOLEAN NOT NULL DEFAULT TRUE,
    discussion_answer_on_your_discussion BOOLEAN NOT NULL DEFAULT TRUE,
    upvotes BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE public_community_post (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    media JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL
);

CREATE TABLE review_upvote (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id, review_id)
);

CREATE TABLE question_upvote (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id, question_id)
);

CREATE TABLE answer_upvote (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id, answer_id)
);

CREATE TABLE discussion_upvote (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id, discussion_id)
);

CREATE TABLE discussion_answer_upvote (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id, discussion_answer_id)
);

CREATE TYPE report_status_enum AS ENUM ('OPEN', 'REVIEWING', 'RESOLVED', 'REJECTED');

CREATE TABLE review_report (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (account_id, review_id)
);

CREATE TABLE question_report (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (account_id, question_id)
);

CREATE TABLE answer_report (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (account_id, answer_id)
);

CREATE TABLE discussion_report (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'OPEN',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (account_id, discussion_id)
);

CREATE TABLE discussion_answer_report (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'OPEN',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,
    UNIQUE (account_id, discussion_answer_id)
);

--constraints for data integrity
--foreign key on delete behaviors review user product?

--triggers for updating fields
--indexes for performance optimization
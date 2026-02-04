CREATE EXTENSION unaccent;

CREATE TABLE category (
    category_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL,
    parent_category_id INT REFERENCES category(category_id) DEFAULT NULL
);

CREATE TABLE brand (
    brand_id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
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
    CONSTRAINT unique_product_name_brand UNIQUE(name, brand_id),
    CHECK (rating >= 0 AND rating <= 5),
    CHECK (price >= 0)
);

CREATE TABLE product_shade (
    product_shade_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
    shade_name TEXT NOT NULL,
    shade_hex_code TEXT,
    shade_number INT,
    image_link TEXT,
    product_link TEXT,

    UNIQUE (product_id, shade_name),
    UNIQUE (product_shade_id, product_id)
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

CREATE TABLE account (
    account_id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    email CITEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    date_joined TIMESTAMPTZ DEFAULT NOW(),
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    unread_notifications_count INT DEFAULT 0
);

--triggers to update followers_count, following_count, and unread_notifications_count in account table

CREATE TABLE wishlist (
    wishlist_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) NOT NULL ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (account_id)
);

CREATE TABLE wishlist_item (
    wishlist_item_id SERIAL PRIMARY KEY,
    wishlist_id INT NOT NULL REFERENCES wishlist(wishlist_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
    shade_id INT,
    added_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE (wishlist_id, product_id, shade_id),
    FOREIGN KEY (shade_id, product_id)
        REFERENCES product_shade(product_shade_id, product_id)
        ON DELETE SET NULL
);

CREATE TYPE routine_category_enum AS ENUM ('skincare', 'makeup', 'haircare', 'bodycare', 'other');

CREATE TABLE routine (
    routine_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) NOT NULL ON DELETE CASCADE,
    name TEXT NOT NULL,
    notes TEXT,
    category routine_category_enum NOT NULL DEFAULT 'other',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, name)
);

CREATE TABLE routine_item (
    routine_item_id BIGSERIAL PRIMARY KEY,
    routine_id INT NOT NULL REFERENCES routine(routine_id) ON DELETE CASCADE,

    product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
    shade_id INT,

    step_order INT NOT NULL,
    notes TEXT,

    valid_from TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    valid_to   TIMESTAMPTZ NULL, -- NULL = current version

    created_by INT REFERENCES account(account_id) ON DELETE SET NULL,

    CHECK (step_order >= 1),
    CHECK (valid_to IS NULL OR valid_to > valid_from),

    FOREIGN KEY (shade_id, product_id)
        REFERENCES product_shade(product_shade_id, product_id)
        ON DELETE SET NULL
);
CREATE UNIQUE INDEX uniq_current_step_per_routine
  ON routine_item (routine_id, step_order)
  WHERE valid_to IS NULL;
--When someone “removes” an item: you just set valid_to = now() (no special removed_at column needed)
--When they “add” it again later: you insert a new row with a new valid_from

CREATE TABLE routine_image (
    routine_image_id SERIAL PRIMARY KEY,
    routine_id INT REFERENCES routine(routine_id) NOT NULL ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ DEFAULT NOW()
);

--add routine item schedules later

CREATE TABLE review (
    review_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE SET NULL,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    product_shade_id INT,
    rating NUMERIC(2, 1) CHECK (rating >= 0 AND rating <= 5),
    review_text TEXT,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (product_shade_id, product_id)
        REFERENCES product_shade(product_shade_id, product_id)
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
    review_image_id SERIAL PRIMARY KEY,
    review_id INT REFERENCES review(review_id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE question (
    question_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE SET NULL,
    product_id INT REFERENCES product(product_id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    answered BOOLEAN DEFAULT FALSE,
    upvote_count INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);
--triggers to update 'answered' field in question table when an answer is added or removed

CREATE TABLE answer (
    answer_id SERIAL PRIMARY KEY,
    question_id INT REFERENCES question(question_id) ON DELETE CASCADE,
    account_id INT REFERENCES account(account_id) ON DELETE SET NULL,
    answer_text TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE discussion (
    discussion_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE discussion_answer (
    discussion_answer_id SERIAL PRIMARY KEY,
    discussion_id INT REFERENCES discussion(discussion_id) ON DELETE CASCADE,
    account_id INT REFERENCES account(account_id) ON DELETE SET NULL,
    parent_discussion_answer_id INT REFERENCES discussion_answer(discussion_answer_id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    helpful_count INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE
);

CREATE TABLE user_discussion_pin (
  user_discussion_pin_id SERIAL PRIMARY KEY,
  discussion_id INT REFERENCES discussion(discussion_id) ON DELETE CASCADE,
  account_id INT REFERENCES account(account_id) ON DELETE CASCADE,
  pinned_at TIMESTAMPTZ DEFAULT NOW(),
  UNIQUE (discussion_id, account_id)
);

CREATE TABLE user_follow (
    user_follow_id SERIAL PRIMARY KEY,
    follower_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    following_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    followed_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (follower_id, following_id),
    CHECK (follower_id <> following_id)
);

CREATE TABLE notification (
  notification_id SERIAL PRIMARY KEY,
  recipient_id INT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  actor_id INT NULL REFERENCES account(account_id) ON DELETE SET NULL,
  type TEXT NOT NULL,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  payload JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE TABLE product_question_notification (
    notification_id INT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product(product_id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE
);

CREATE TABLE question_answered_notification (
    notification_id INT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(answer_id) ON DELETE CASCADE
);

CREATE TABLE discussion_answered_notification (
    notification_id INT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(discussion_id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(discussion_answer_id) ON DELETE CASCADE
);

CREATE TABLE review_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(review_id) ON DELETE CASCADE
);

CREATE TABLE question_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE
);

CREATE TABLE answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(answer_id) ON DELETE CASCADE
);

CREATE TABLE discussion_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(discussion_id) ON DELETE CASCADE
);

CREATE TABLE discussion_answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(notification_id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(discussion_answer_id) ON DELETE CASCADE
);

CREATE TABLE user_notification_pref (
    account_id INT PRIMARY KEY REFERENCES account(account_id) ON DELETE CASCADE,
    question_on_routine_product BOOLEAN NOT NULL DEFAULT TRUE,
    answer_on_your_question BOOLEAN NOT NULL DEFAULT TRUE,
    discussion_answer_on_your_discussion BOOLEAN NOT NULL DEFAULT TRUE,
    upvotes BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE public_community_post (
    public_community_post_id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    media JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL
);

CREATE TABLE review_upvote (
  account_id INT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  review_id INT NOT NULL REFERENCES review(review_id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (account_id, review_id)
);

CREATE TABLE question_upvote (
  account_id INT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  question_id INT NOT NULL REFERENCES question(question_id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (account_id, question_id)
);

CREATE TABLE answer_upvote (
  account_id INT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  answer_id INT NOT NULL REFERENCES answer(answer_id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (account_id, answer_id)
);

CREATE TABLE discussion_upvote (
  account_id INT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  discussion_id INT NOT NULL REFERENCES discussion(discussion_id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (account_id, discussion_id)
);

CREATE TABLE discussion_answer_upvote (
  account_id INT NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
  discussion_answer_id INT NOT NULL REFERENCES discussion_answer(discussion_answer_id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  PRIMARY KEY (account_id, discussion_answer_id)
);

CREATE TYPE report_status_enum AS ENUM ('open', 'reviewing', 'resolved', 'rejected');

CREATE TABLE review_report (
    report_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'open',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ NULL,
    PRIMARY KEY (report_id, account_id)
);

CREATE TABLE question_report (
    report_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'open',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ NULL,
    PRIMARY KEY (report_id, account_id)
);

CREATE TABLE answer_report (
    report_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'open',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ NULL,
    PRIMARY KEY (report_id, account_id)
);

CREATE TABLE discussion_report (
    report_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'open',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ NULL,
    PRIMARY KEY (report_id, account_id)
);

CREATE TABLE discussion_answer_report (
    report_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(account_id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum DEFAULT 'open',
    created_at TIMESTAMPTZ DEFAULT NOW(),
    resolved_at TIMESTAMPTZ NULL,
    PRIMARY KEY (report_id, account_id)
);

--triggers for updated_at fields and other automatic updates
--other triggers?
--constraints for data integrity
--foreign key on delete behaviors review user product?
--handle reviews with shades and not shades



--indexes for performance optimization
-- ================================================================
-- Extensions
-- ================================================================

CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS citext;





-- ================================================================
-- Products, Categories, Brands, Ingredients
-- ================================================================

CREATE TABLE category (
    id SERIAL PRIMARY KEY,
    name CITEXT UNIQUE NOT NULL,
    parent_category_id INT REFERENCES category(id) ON DELETE SET NULL,

    CHECK (length(trim(name::text)) > 0),
    CHECK (parent_category_id IS NULL OR parent_category_id <> id)
);
CREATE INDEX idx_category_parent ON category (parent_category_id);

-------------------------------------------------------------------
CREATE TABLE brand (
    id SERIAL PRIMARY KEY,
    name CITEXT UNIQUE NOT NULL,

    CHECK (length(trim(name::text)) > 0)
);

-------------------------------------------------------------------
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name CITEXT NOT NULL,
    brand_id INT NOT NULL REFERENCES brand(id),
    category_id INT NOT NULL REFERENCES category(id),
    price NUMERIC(10, 2),
    image_link TEXT,
    product_link TEXT,
    rating NUMERIC(2, 1),
    raw_ingredients TEXT,
    may_contain_raw_ingredients TEXT,
    review_count INT NOT NULL DEFAULT 0,

    UNIQUE(name, brand_id),

    CHECK (rating >= 0 AND rating <= 5),
    CHECK (price >= 0),

    CHECK (length(trim(name::text)) > 0),
    CHECK (image_link IS NULL OR length(trim(image_link)) > 0),
    CHECK (product_link IS NULL OR length(trim(product_link)) > 0)
);
CREATE INDEX idx_product_brand ON product (brand_id);
CREATE INDEX idx_product_category ON product (category_id);

CREATE TABLE product_shade (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_name CITEXT NOT NULL,
    shade_hex_code TEXT,
    shade_number INT,
    image_link TEXT,
    product_link TEXT,

    UNIQUE (product_id, shade_name), -- prevents duplicate shade names for the same product
    UNIQUE (id, product_id), -- allows other tables to ensure shade belongs to product in FKs

    CHECK (length(trim(shade_name::text)) > 0),
    CHECK (shade_hex_code IS NULL OR shade_hex_code ~* '^#[0-9A-F]{6}$')
);
CREATE INDEX idx_product_shade_product ON product_shade (product_id);

------------------------------------------------------------------
CREATE TABLE ingredient (
    id SERIAL PRIMARY KEY,
    name CITEXT UNIQUE NOT NULL,
    canonical_id INT REFERENCES ingredient(id) ON DELETE SET NULL,
    is_common_allergen BOOLEAN NOT NULL DEFAULT FALSE,
    is_fragrance BOOLEAN NOT NULL DEFAULT FALSE,

    CHECK (length(trim(name::text)) > 0),
    CHECK (canonical_id IS NULL OR canonical_id <> id)
);
CREATE INDEX idx_ingredient_canonical ON ingredient (canonical_id);

CREATE TABLE product_ingredient (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    ingredient_id INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    position INT NOT NULL,
    UNIQUE (product_id, ingredient_id),
    UNIQUE (product_id, position),

    CHECK (position >= 1)
);
CREATE INDEX idx_product_ingredient_product ON product_ingredient (product_id);
CREATE INDEX idx_product_ingredient_ingredient ON product_ingredient (ingredient_id);

CREATE TABLE may_contain_ingredient (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    ingredient_id INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    UNIQUE (product_id, ingredient_id)
);
CREATE INDEX idx_may_contain_ingredient_product ON may_contain_ingredient (product_id);
CREATE INDEX idx_may_contain_ingredient_ingredient ON may_contain_ingredient (ingredient_id);





-- ================================================================
-- User Accounts, Wishlists, Routines
-- ================================================================

CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    username CITEXT UNIQUE NOT NULL,
    email CITEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    display_name TEXT,
    avatar_link TEXT,

    date_joined TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_private BOOLEAN NOT NULL DEFAULT FALSE,

    followers_count INT NOT NULL DEFAULT 0,
    following_count INT NOT NULL DEFAULT 0,
    unread_notifications_count INT NOT NULL DEFAULT 0,

    CHECK (length(trim(username)) > 0),
    CHECK (length(trim(email)) > 0),

    CHECK (followers_count >= 0),
    CHECK (following_count >= 0),
    CHECK (unread_notifications_count >= 0),

    CHECK (username ~* '^[a-zA-Z0-9_]+$'),
    CHECK (length(username) <= 30)
);

CREATE TABLE account_follow (
    id SERIAL PRIMARY KEY,
    follower_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    following_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    followed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (follower_id, following_id),
    CHECK (follower_id <> following_id)
);
CREATE INDEX idx_account_follow_follower ON account_follow (follower_id);
CREATE INDEX idx_account_follow_following ON account_follow (following_id);

CREATE TABLE account_notification_pref (
    account_id INT PRIMARY KEY REFERENCES account(id) ON DELETE CASCADE,
    question_on_routine_product BOOLEAN NOT NULL DEFAULT TRUE,
    answer_on_your_question BOOLEAN NOT NULL DEFAULT TRUE,
    discussion_answer_on_your_discussion BOOLEAN NOT NULL DEFAULT TRUE,
    upvotes BOOLEAN NOT NULL DEFAULT TRUE
);

-------------------------------------------------------------------
CREATE TABLE wishlist (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (account_id)
);
CREATE INDEX idx_wishlist_account ON wishlist (account_id);

CREATE TABLE wishlist_item (
    id SERIAL PRIMARY KEY,
    wishlist_id INT NOT NULL REFERENCES wishlist(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_id INT,
    added_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (shade_id IS NULL OR product_id IS NOT NULL),

    UNIQUE (wishlist_id, product_id, shade_id),

    FOREIGN KEY (shade_id, product_id)
    REFERENCES product_shade(id, product_id)
    ON DELETE SET NULL
);
CREATE INDEX idx_wishlist_item_wishlist ON wishlist_item (wishlist_id);
CREATE INDEX idx_wishlist_item_product ON wishlist_item (product_id);

CREATE UNIQUE INDEX uq_wishlist_item_product_noshade
ON wishlist_item (wishlist_id, product_id)
WHERE shade_id IS NULL;

-------------------------------------------------------------------
CREATE TABLE routine (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    notes TEXT,
    category_id INT NOT NULL REFERENCES category(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (length(trim(name)) > 0),
    CHECK (notes IS NULL OR length(trim(notes)) > 0),

    UNIQUE (account_id, name)
);
CREATE INDEX idx_routine_account ON routine (account_id);
CREATE INDEX idx_routine_category ON routine (category_id);

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
    CHECK (shade_id IS NULL OR product_id IS NOT NULL),

    FOREIGN KEY (shade_id, product_id)
    REFERENCES product_shade(id, product_id)
    ON DELETE SET NULL
);
CREATE INDEX idx_routine_item_routine ON routine_item (routine_id);
CREATE INDEX idx_routine_item_product ON routine_item (product_id);
CREATE INDEX idx_routine_item_account ON routine_item (created_by);

CREATE UNIQUE INDEX uniq_current_step_per_routine
ON routine_item (routine_id, step_order)
WHERE valid_to IS NULL;

CREATE UNIQUE INDEX uq_current_routine_item_product_noshade
ON routine_item (routine_id, product_id)
WHERE valid_to IS NULL AND shade_id IS NULL;

CREATE UNIQUE INDEX uq_current_routine_item_product_shade
ON routine_item (routine_id, product_id, shade_id)
WHERE valid_to IS NULL AND shade_id IS NOT NULL;
--When someone “removes” an item: you just set valid_to = now() (no special removed_at column needed)
--When they “add” it again later: you insert a new row with a new valid_from

CREATE TABLE routine_image (
    id SERIAL PRIMARY KEY,
    routine_id INT NOT NULL REFERENCES routine(id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    display_order INT NOT NULL DEFAULT 1,

    UNIQUE (routine_id, image_link),

    CHECK (length(trim(image_link)) > 0)
);
CREATE INDEX idx_routine_image_routine ON routine_image (routine_id);

--add routine item schedules later





--================================================================
-- Product Page Content: Reviews, Q&A
--================================================================

CREATE TABLE review (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    product_shade_id INT,
    rating NUMERIC(3, 2) CHECK (rating >= 0 AND rating <= 5),
    review_title TEXT,
    review_text TEXT,
    helpful_count INT NOT NULL DEFAULT 0,
    reported_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    approved BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (helpful_count >= 0),
    CHECK (reported_count >= 0),
    CHECK (review_text IS NULL OR length(trim(review_text)) > 0),

    CHECK (
        (product_shade_id IS NULL AND product_id IS NOT NULL) OR
        (product_shade_id IS NOT NULL AND product_id IS NOT NULL)
    ),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    FOREIGN KEY (product_shade_id, product_id)
    REFERENCES product_shade(id, product_id)
    ON DELETE SET NULL
);
CREATE INDEX idx_review_product ON review (product_id);
CREATE INDEX idx_review_account ON review (account_id);

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
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    display_order INT NOT NULL DEFAULT 1,

    UNIQUE (review_id, image_link),

    CHECK (length(trim(image_link)) > 0)
);
CREATE INDEX idx_review_image_review ON review_image (review_id);

-----------------------------------------------------------------------------
CREATE TABLE question (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    answered BOOLEAN NOT NULL DEFAULT FALSE,
    upvote_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,
    answer_count INT NOT NULL DEFAULT 0,

    CHECK (upvote_count >= 0),
    CHECK (reported_count >= 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (length(trim(question_text)) > 0)
);
CREATE INDEX idx_question_product ON question (product_id);
CREATE INDEX idx_question_account ON question (account_id);
--triggers to update 'answered' field in question table when an answer is added or removed

CREATE TABLE answer (
    id SERIAL PRIMARY KEY,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    answer_text TEXT NOT NULL,
    helpful_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (helpful_count >= 0),
    CHECK (reported_count >= 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (length(trim(answer_text)) > 0)
);
CREATE INDEX idx_answer_question ON answer (question_id);
CREATE INDEX idx_answer_account ON answer (account_id);





-- ===========================================================================
-- Discussions
-- ===========================================================================

CREATE TABLE discussion (
    id SERIAL PRIMARY KEY,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,
    reply_count INT NOT NULL DEFAULT 0,
    upvote_count INT NOT NULL DEFAULT 0,

    CHECK (reported_count >= 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (length(trim(title)) > 0),
    CHECK (length(trim(content)) > 0)
);
CREATE INDEX idx_discussion_account ON discussion (account_id);

CREATE TABLE discussion_answer (
    id SERIAL PRIMARY KEY,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    account_id INT REFERENCES account(id) ON DELETE SET NULL,
    parent_discussion_answer_id INT,
    content TEXT NOT NULL,
    helpful_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (helpful_count >= 0),
    CHECK (reported_count >= 0),

    CHECK (length(trim(content)) > 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (parent_discussion_answer_id IS NULL OR parent_discussion_answer_id <> id),

    UNIQUE (id, discussion_id),

    FOREIGN KEY (parent_discussion_answer_id, discussion_id)
    REFERENCES discussion_answer(id, discussion_id)
    ON DELETE SET NULL
);
CREATE INDEX idx_discussion_answer_discussion ON discussion_answer (discussion_id);
CREATE INDEX idx_discussion_answer_account ON discussion_answer (account_id);

CREATE TABLE user_discussion_pin (
    id SERIAL PRIMARY KEY,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    pinned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (discussion_id, account_id)
);
CREATE INDEX idx_user_discussion_pin_discussion ON user_discussion_pin (discussion_id);
CREATE INDEX idx_user_discussion_pin_account ON user_discussion_pin (account_id);





-- ===========================================================================
-- Upvotes, reports, notifications
-- ===========================================================================

CREATE TYPE upvote_target_enum AS ENUM (
    'REVIEW',
    'QUESTION',
    'ANSWER',
    'DISCUSSION',
    'DISCUSSION_ANSWER'
);

CREATE TABLE upvote (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    target_type upvote_target_enum NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_upvote_account ON upvote (account_id);
CREATE INDEX idx_upvote_target_type ON upvote (target_type);

CREATE TABLE review_upvote (
    upvote_id INT PRIMARY KEY NOT NULL REFERENCES upvote(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE,

    UNIQUE (upvote_id, review_id)
);
CREATE INDEX idx_review_upvote_review ON review_upvote (review_id);

CREATE TABLE question_upvote (
    upvote_id INT PRIMARY KEY NOT NULL REFERENCES upvote(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE,

    UNIQUE (upvote_id, question_id)
);
CREATE INDEX idx_question_upvote_question ON question_upvote (question_id);

CREATE TABLE answer_upvote (
    upvote_id INT PRIMARY KEY NOT NULL REFERENCES upvote(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE,

    UNIQUE (upvote_id, answer_id)
);
CREATE INDEX idx_answer_upvote_answer ON answer_upvote (answer_id);

CREATE TABLE discussion_upvote (
    upvote_id INT PRIMARY KEY NOT NULL REFERENCES upvote(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,

    UNIQUE (upvote_id, discussion_id)
);
CREATE INDEX idx_discussion_upvote_discussion ON discussion_upvote (discussion_id);

CREATE TABLE discussion_answer_upvote (
    upvote_id INT PRIMARY KEY NOT NULL REFERENCES upvote(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE,

    UNIQUE (upvote_id, discussion_answer_id)
);
CREATE INDEX idx_discussion_answer_upvote_discussion_answer ON discussion_answer_upvote (discussion_answer_id);

------------------------------------------------------------------------------
CREATE TYPE report_target_enum AS ENUM (
    'REVIEW',
    'QUESTION',
    'ANSWER',
    'DISCUSSION',
    'DISCUSSION_ANSWER'
);

CREATE TYPE report_status_enum AS ENUM ('OPEN', 'REVIEWING', 'RESOLVED', 'REJECTED');

CREATE TABLE report (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    target_type report_target_enum NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL
);
CREATE INDEX idx_report_account ON report (account_id);
CREATE INDEX idx_report_status ON report (status);
CREATE INDEX idx_report_target_type ON report (target_type);

CREATE TABLE review_report (
    report_id INT PRIMARY KEY NOT NULL REFERENCES report(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE
);
CREATE INDEX idx_review_report_review ON review_report (review_id);

CREATE TABLE question_report (
    report_id INT PRIMARY KEY NOT NULL REFERENCES report(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);
CREATE INDEX idx_question_report_question ON question_report (question_id);

CREATE TABLE answer_report (
    report_id INT PRIMARY KEY NOT NULL REFERENCES report(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_answer_report_answer ON answer_report (answer_id);

CREATE TABLE discussion_report (
    report_id INT PRIMARY KEY NOT NULL REFERENCES report(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_report_discussion ON discussion_report (discussion_id);

CREATE TABLE discussion_answer_report (
    report_id INT PRIMARY KEY NOT NULL REFERENCES report(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_answer_report_discussion_answer ON discussion_answer_report (discussion_answer_id);

------------------------------------------------------------------------------
CREATE TYPE notification_type_enum AS ENUM (
    'PRODUCT_QUESTION',
    'QUESTION_ANSWERED',
    'DISCUSSION_ANSWERED',
    'REVIEW_UPVOTED',
    'QUESTION_UPVOTED',
    'ANSWER_UPVOTED',
    'DISCUSSION_UPVOTED',
    'DISCUSSION_ANSWER_UPVOTED'
);

CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    recipient_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    actor_id INT NULL REFERENCES account(id) ON DELETE SET NULL,
    type notification_type_enum NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,

    CHECK (jsonb_typeof(payload) = 'object')
);
CREATE INDEX idx_notification_recipient ON notification (recipient_id);
CREATE INDEX idx_notification_actor ON notification (actor_id);
CREATE INDEX idx_notification_type ON notification (type);

CREATE TABLE product_question_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);
CREATE INDEX idx_product_question_notification_product ON product_question_notification (product_id);
CREATE INDEX idx_product_question_notification_question ON product_question_notification (question_id);

CREATE TABLE question_answered_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_question_answered_notification_question ON question_answered_notification (question_id);
CREATE INDEX idx_question_answered_notification_answer ON question_answered_notification (answer_id);

CREATE TABLE discussion_answered_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_answered_notification_discussion ON discussion_answered_notification (discussion_id);
CREATE INDEX idx_discussion_answered_notification_discussion_answer ON discussion_answered_notification (discussion_answer_id);

CREATE TABLE review_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE
);
CREATE INDEX idx_review_upvoted_notification_review ON review_upvoted_notification (review_id);

CREATE TABLE question_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    question_id INT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);
CREATE INDEX idx_question_upvoted_notification_question ON question_upvoted_notification (question_id);

CREATE TABLE answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    answer_id INT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_answer_upvoted_notification_answer ON answer_upvoted_notification (answer_id);

CREATE TABLE discussion_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_id INT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_upvoted_notification_discussion ON discussion_upvoted_notification (discussion_id);

CREATE TABLE discussion_answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_answer_id INT NOT NULL REFERENCES discussion_answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_answer_upvoted_notification_discussion_answer ON discussion_answer_upvoted_notification (discussion_answer_id);





-- ===========================================================================
-- Community Posts: Public
-- ===========================================================================

CREATE TABLE public_community_post (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    media JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,

    CHECK (length(trim(title)) > 0),
    CHECK (length(trim(content)) > 0)
);




-- ===========================================================================
-- Activity Feed (followed accounts only)
-- ===========================================================================

CREATE TYPE activity_type_enum AS ENUM (
    'REVIEW',
    'ROUTINE_ITEM',
    'ROUTINE_IMAGE',
    'WISHLIST_ITEM'
);

CREATE TABLE activity (
    id SERIAL PRIMARY KEY,
    actor_id INT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    type activity_type_enum NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,

    CHECK (jsonb_typeof(payload) = 'object')
);
CREATE INDEX idx_activity_actor ON activity (actor_id);

CREATE TABLE review_activity (
    activity_id INT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    review_id INT NOT NULL REFERENCES review(id) ON DELETE CASCADE
);
CREATE INDEX idx_review_activity_review ON review_activity (review_id);

CREATE TABLE routine_item_activity (
    activity_id INT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    routine_item_id INT NOT NULL REFERENCES routine_item(id) ON DELETE CASCADE
);
CREATE INDEX idx_routine_item_activity_routine_item ON routine_item_activity (routine_item_id);

CREATE TABLE routine_image_activity (
    activity_id INT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    routine_image_id INT NOT NULL REFERENCES routine_image(id) ON DELETE CASCADE
);
CREATE INDEX idx_routine_image_activity_routine_image ON routine_image_activity (routine_image_id);

CREATE TABLE wishlist_item_activity (
    activity_id INT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    wishlist_item_id INT NOT NULL REFERENCES wishlist_item(id) ON DELETE CASCADE
);
CREATE INDEX idx_wishlist_item_activity_wishlist_item ON wishlist_item_activity (wishlist_item_id);





--triggers for updating fields
--indexes for performance optimization
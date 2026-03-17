-- ================================================================
-- Extensions
-- ================================================================

CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS citext;





-- ================================================================
-- Products, Categories, Brands, Ingredients
-- ================================================================

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name CITEXT UNIQUE NOT NULL,
    parent_category_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (length(trim(name::text)) > 0),
    CHECK (parent_category_id IS NULL OR parent_category_id <> id)
);
CREATE INDEX idx_category_parent ON category (parent_category_id);

-------------------------------------------------------------------
CREATE TABLE brand (
    id BIGSERIAL PRIMARY KEY,
    name CITEXT UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_discontinued BOOLEAN NOT NULL DEFAULT FALSE,

    CHECK (length(trim(name::text)) > 0)
);

-------------------------------------------------------------------
CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name CITEXT NOT NULL,
    brand_id BIGINT NOT NULL REFERENCES brand(id),
    category_id BIGINT NOT NULL REFERENCES category(id),
    price NUMERIC(10, 2),
    image_link TEXT,
    product_link TEXT,
    rating NUMERIC(2, 1),
    raw_ingredients TEXT,
    may_contain_raw_ingredients TEXT,
    review_count INT NOT NULL DEFAULT 0,
    is_discontinued BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

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
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_name CITEXT NOT NULL,
    shade_hex_code TEXT,
    shade_number INT,
    image_link TEXT,
    product_link TEXT,
    discontinued BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (product_id, shade_name), -- prevents duplicate shade names for the same product
    UNIQUE (id, product_id), -- allows other tables to ensure shade belongs to product in FKs

    CHECK (length(trim(shade_name::text)) > 0),
    CHECK (shade_hex_code IS NULL OR shade_hex_code ~* '^#[0-9A-F]{6}$')
);
CREATE INDEX idx_product_shade_product ON product_shade (product_id);

------------------------------------------------------------------
CREATE TABLE ingredient (
    id BIGSERIAL PRIMARY KEY,
    name CITEXT UNIQUE NOT NULL,
    canonical_id BIGINT REFERENCES ingredient(id) ON DELETE SET NULL,
    is_common_allergen BOOLEAN NOT NULL DEFAULT FALSE,
    is_common_irritant BOOLEAN NOT NULL DEFAULT FALSE,
    is_fragrance BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (length(trim(name::text)) > 0),
    CHECK (canonical_id IS NULL OR canonical_id <> id)
);
CREATE INDEX idx_ingredient_canonical ON ingredient (canonical_id);

CREATE TABLE product_ingredient (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    position INT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (product_id, ingredient_id),
    UNIQUE (product_id, position),

    CHECK (position >= 1)
);
CREATE INDEX idx_product_ingredient_product ON product_ingredient (product_id);
CREATE INDEX idx_product_ingredient_ingredient ON product_ingredient (ingredient_id);

CREATE TABLE may_contain_ingredient (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (product_id, ingredient_id)
);
CREATE INDEX idx_may_contain_ingredient_product ON may_contain_ingredient (product_id);
CREATE INDEX idx_may_contain_ingredient_ingredient ON may_contain_ingredient (ingredient_id);





-- ================================================================
-- ================================================================
-- User Accounts, Wishlists, Routines

CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    username CITEXT NOT NULL,
    email CITEXT NOT NULL,
    password_hash TEXT NOT NULL,
    display_name TEXT,
    avatar_link TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), --trigger to only change when username, email, password, dispay_name, or avatar_link changes, not when followers_count etc. change
    deleted_at TIMESTAMPTZ NULL,

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
    CHECK (length(username) <= 30),

    CHECK (display_name IS NULL OR length(display_name) <= 100)
);
CREATE UNIQUE INDEX uq_account_username_active
ON account (username)
WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uq_account_email_active
ON account (email)
WHERE deleted_at IS NULL;

CREATE TABLE account_follow (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    followed_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (follower_id, followed_id),
    CHECK (follower_id <> followed_id)
);
CREATE INDEX idx_account_follow_follower ON account_follow (follower_id);
CREATE INDEX idx_account_follow_followed ON account_follow (followed_id);

CREATE TABLE account_notification_preference (
    account_id BIGINT PRIMARY KEY REFERENCES account(id) ON DELETE CASCADE,
    question_on_routine_product BOOLEAN NOT NULL DEFAULT TRUE,
    answer_on_your_question BOOLEAN NOT NULL DEFAULT TRUE,
    discussion_comment_on_your_discussion BOOLEAN NOT NULL DEFAULT TRUE,
    discussion_comment_on_your_discussion_comment BOOLEAN NOT NULL DEFAULT TRUE,
    upvotes BOOLEAN NOT NULL DEFAULT TRUE
);

-------------------------------------------------------------------

CREATE TABLE product_purchase (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_id BIGINT,
    times_purchased INT NOT NULL DEFAULT 1,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_purchased_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CHECK (times_purchased >= 1),

    FOREIGN KEY (shade_id, product_id)
    REFERENCES product_shade(id, product_id)
    ON DELETE SET NULL,

    UNIQUE (account_id, product_id, shade_id)
);
CREATE INDEX idx_product_purchase_account ON product_purchase (account_id);
CREATE INDEX idx_product_purchase_product ON product_purchase (product_id);

CREATE UNIQUE INDEX uq_product_purchase_product_noshade ON product_purchase (account_id, product_id) WHERE shade_id IS NULL;

-------------------------------------------------------------------
CREATE TABLE wishlist (
    account_id BIGINT PRIMARY KEY NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE wishlist_item (
    id BIGSERIAL PRIMARY KEY,
    wishlist_id BIGINT NOT NULL REFERENCES wishlist(account_id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_id BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

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

CREATE TYPE time_of_day_enum AS ENUM ('AM', 'PM');

CREATE TYPE occasion_enum AS ENUM ('CASUAL', 'GLAM', 'EVENT', 'OTHER');

CREATE TABLE routine (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES category(id),
    name TEXT,
    notes TEXT,
    time_of_day time_of_day_enum NULL,   -- only used for Skincare
    occasion occasion_enum NULL,         -- only used for Makeup
    is_system BOOLEAN NOT NULL DEFAULT FALSE,     -- true if this is a default system routine, false if created by user
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,

    CHECK (name IS NULL OR length(trim(name)) <= 128)
);
CREATE INDEX idx_routine_account ON routine (account_id);
CREATE INDEX idx_routine_category ON routine (category_id);

-- Trigger function to enforce name NULL for CASUAL/GLAM and not NULL for other occasions
CREATE OR REPLACE FUNCTION routine_name_for_occasion_check()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.occasion IN ('CASUAL', 'GLAM') AND NEW.name IS NOT NULL THEN
        RAISE EXCEPTION 'Routines with occasion % must have name IS NULL', NEW.occasion;
    END IF;

    IF NEW.occasion NOT IN ('CASUAL', 'GLAM') AND NEW.name IS NULL THEN
        RAISE EXCEPTION 'Routines with occasion % must have a name', NEW.occasion;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_routine_name_for_occasion
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION routine_name_for_occasion_check();

-- Trigger function to enforce time_of_day only set for Skincare routines
CREATE OR REPLACE FUNCTION routine_time_of_day_check()
RETURNS TRIGGER AS $$
DECLARE
    skincare_category_id BIGINT;
BEGIN
    SELECT id INTO skincare_category_id FROM category WHERE name='Skincare';

    IF NEW.time_of_day IS NOT NULL AND NEW.category_id <> skincare_category_id THEN
        RAISE EXCEPTION 'time_of_day can only be set for Skincare routines';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_routine_time_of_day
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION routine_time_of_day_check();

-- Trigger function to enforce occasion only set for Makeup routines
CREATE OR REPLACE FUNCTION routine_occasion_check()
RETURNS TRIGGER AS $$
DECLARE
    makeup_category_id BIGINT;
BEGIN
    SELECT id INTO makeup_category_id FROM category WHERE name='Makeup';

    IF NEW.occasion IS NOT NULL AND NEW.category_id <> makeup_category_id THEN
        RAISE EXCEPTION 'occasion can only be set for Makeup routines';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_routine_occasion
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION routine_occasion_check();

-- Trigger function to enforce only base categories
CREATE OR REPLACE FUNCTION check_base_category_for_routine()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM category WHERE id = NEW.category_id AND parent_category_id IS NOT NULL
    ) THEN
        RAISE EXCEPTION 'Routine category_id % must be a base category (parent_category_id IS NULL)', NEW.category_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER routine_base_category_check
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION check_base_category_for_routine();

-- Only one Haircare routine per account
CREATE OR REPLACE FUNCTION unique_haircare_per_account()
RETURNS TRIGGER AS $$
DECLARE
    count_routines INT;
BEGIN
    IF (NEW.category_id = (SELECT id FROM category WHERE name='Haircare')) THEN
        SELECT COUNT(*) INTO count_routines
        FROM routine
        WHERE account_id = NEW.account_id
          AND category_id = NEW.category_id
          AND deleted_at IS NULL
          AND id <> COALESCE(NEW.id, 0);

        IF count_routines > 0 THEN
            RAISE EXCEPTION 'Each account can have only one Haircare routine';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_unique_haircare_per_account
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION unique_haircare_per_account();

-- Only one Skincare AM routine per account
CREATE OR REPLACE FUNCTION unique_skincare_am_per_account()
RETURNS TRIGGER AS $$
DECLARE
    count_routines INT;
    skincare_cat_id BIGINT;
BEGIN
    SELECT id INTO skincare_cat_id FROM category WHERE name='Skincare';
    IF NEW.category_id = skincare_cat_id AND NEW.time_of_day = 'AM' THEN
        SELECT COUNT(*) INTO count_routines
        FROM routine
        WHERE account_id = NEW.account_id
          AND category_id = skincare_cat_id
          AND time_of_day = 'AM'
          AND deleted_at IS NULL
          AND id <> COALESCE(NEW.id, 0);

        IF count_routines > 0 THEN
            RAISE EXCEPTION 'Each account can have only one Skincare AM routine';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_unique_skincare_am_per_account
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION unique_skincare_am_per_account();

-- Only one Skincare PM routine per account
CREATE OR REPLACE FUNCTION unique_skincare_pm_per_account()
RETURNS TRIGGER AS $$
DECLARE
    count_routines INT;
    skincare_cat_id BIGINT;
BEGIN
    SELECT id INTO skincare_cat_id FROM category WHERE name='Skincare';
    IF NEW.category_id = skincare_cat_id AND NEW.time_of_day = 'PM' THEN
        SELECT COUNT(*) INTO count_routines
        FROM routine
        WHERE account_id = NEW.account_id
          AND category_id = skincare_cat_id
          AND time_of_day = 'PM'
          AND deleted_at IS NULL
          AND id <> COALESCE(NEW.id, 0);

        IF count_routines > 0 THEN
            RAISE EXCEPTION 'Each account can have only one Skincare PM routine';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_unique_skincare_pm_per_account
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION unique_skincare_pm_per_account();

-- Only one CASUAL Makeup routine per account, name must be NULL
CREATE OR REPLACE FUNCTION unique_makeup_casual_per_account()
RETURNS TRIGGER AS $$
DECLARE
    count_routines INT;
    makeup_cat_id BIGINT;
BEGIN
    SELECT id INTO makeup_cat_id FROM category WHERE name='Makeup';
    IF NEW.category_id = makeup_cat_id AND NEW.occasion = 'CASUAL' THEN
        IF NEW.name IS NOT NULL THEN
            RAISE EXCEPTION 'CASUAL Makeup routine cannot have a name';
        END IF;

        SELECT COUNT(*) INTO count_routines
        FROM routine
        WHERE account_id = NEW.account_id
          AND category_id = makeup_cat_id
          AND occasion = 'CASUAL'
          AND deleted_at IS NULL
          AND id <> COALESCE(NEW.id, 0);

        IF count_routines > 0 THEN
            RAISE EXCEPTION 'Each account can have only one CASUAL Makeup routine';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_unique_makeup_casual_per_account
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION unique_makeup_casual_per_account();

-- Only one GLAM Makeup routine per account, name must be NULL
CREATE OR REPLACE FUNCTION unique_makeup_glam_per_account()
RETURNS TRIGGER AS $$
DECLARE
    count_routines INT;
    makeup_cat_id BIGINT;
BEGIN
    SELECT id INTO makeup_cat_id FROM category WHERE name='Makeup';
    IF NEW.category_id = makeup_cat_id AND NEW.occasion = 'GLAM' THEN
        IF NEW.name IS NOT NULL THEN
            RAISE EXCEPTION 'GLAM Makeup routine cannot have a name';
        END IF;

        SELECT COUNT(*) INTO count_routines
        FROM routine
        WHERE account_id = NEW.account_id
          AND category_id = makeup_cat_id
          AND occasion = 'GLAM'
          AND deleted_at IS NULL
          AND id <> COALESCE(NEW.id, 0);

        IF count_routines > 0 THEN
            RAISE EXCEPTION 'Each account can have only one GLAM Makeup routine';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_unique_makeup_glam_per_account
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION unique_makeup_glam_per_account();

-- Prevent deletion of system routines
CREATE OR REPLACE FUNCTION prevent_system_routine_deletion()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.is_system THEN
        RAISE EXCEPTION 'Cannot delete a system routine';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_system_routine_deletion
BEFORE DELETE ON routine
FOR EACH ROW
EXECUTE FUNCTION prevent_system_routine_deletion();

CREATE TABLE routine_image (
    id BIGSERIAL PRIMARY KEY,
    routine_id BIGINT NOT NULL REFERENCES routine(id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    display_order INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (routine_id, image_link),

    CHECK (length(trim(image_link)) > 0)
);
CREATE INDEX idx_routine_image_routine ON routine_image (routine_id);

CREATE TABLE routine_item (
    id BIGSERIAL PRIMARY KEY,
    routine_id BIGINT NOT NULL REFERENCES routine(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    shade_id BIGINT REFERENCES product_shade(id) ON DELETE SET NULL,
    step_order INT NOT NULL,
    occurrence TEXT,    -- e.g., "AM", "PM" or custom notes
    notes TEXT,         -- per-item notes
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    valid_from TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    valid_to TIMESTAMPTZ NULL,

    CHECK (notes IS NULL OR length(trim(notes)) <= 126)
);
CREATE INDEX idx_routine_item_routine ON routine_item (routine_id);
CREATE INDEX idx_routine_item_product ON routine_item (product_id);
CREATE INDEX idx_routine_item_shade ON routine_item (shade_id);

--================================================================
-- Product Page Content: Reviews, Q&A
--================================================================

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES account(id) ON DELETE SET NULL,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    product_shade_id BIGINT,
    rating NUMERIC(3, 2) CHECK (rating >= 0 AND rating <= 5),
    title TEXT,
    text TEXT,
    upvote_count INT NOT NULL DEFAULT 0,
    reported_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ DEFAULT NULL,
    approved BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (upvote_count >= 0),
    CHECK (reported_count >= 0),
    CHECK (text IS NULL OR length(trim(text)) > 0),
    CHECK (title IS NULL OR length(trim(title)) > 0),
    CHECK (title IS NULL OR length(title) <= 200),

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
    id BIGSERIAL PRIMARY KEY,
    review_id BIGINT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    image_link TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    display_order INT NOT NULL DEFAULT 1,

    UNIQUE (review_id, image_link),

    CHECK (length(trim(image_link)) > 0)
);
CREATE INDEX idx_review_image_review ON review_image (review_id);

-----------------------------------------------------------------------------
CREATE TABLE question (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES account(id) ON DELETE SET NULL,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    is_answered BOOLEAN NOT NULL DEFAULT FALSE,
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

    CHECK (length(trim(text)) > 0)
);
CREATE INDEX idx_question_product ON question (product_id);
CREATE INDEX idx_question_account ON question (account_id);

CREATE TABLE answer (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    account_id BIGINT REFERENCES account(id) ON DELETE SET NULL,
    text TEXT NOT NULL,
    upvote_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (upvote_count >= 0),
    CHECK (reported_count >= 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (length(trim(text)) > 0),

    UNIQUE (question_id, account_id)
);
CREATE INDEX idx_answer_question ON answer (question_id);
CREATE INDEX idx_answer_account ON answer (account_id);





-- ===========================================================================
-- Discussions
-- ===========================================================================

CREATE TABLE discussion (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES account(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,
    reply_count INT NOT NULL DEFAULT 0,
    upvote_count INT NOT NULL DEFAULT 0,

    CHECK (reported_count >= 0),
    CHECK (upvote_count >= 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (length(trim(title)) > 0),
    CHECK (length(trim(text)) > 0),

    CHECK (length(title) <= 200)
);
CREATE INDEX idx_discussion_account ON discussion (account_id);

CREATE TABLE discussion_comment (
    id BIGSERIAL PRIMARY KEY,
    discussion_id BIGINT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    account_id BIGINT REFERENCES account(id) ON DELETE SET NULL,
    parent_discussion_comment_id BIGINT REFERENCES discussion_comment(id) ON DELETE SET NULL,
    text TEXT NOT NULL,
    upvote_count INT NOT NULL DEFAULT 0,
    reply_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ NULL,
    reported_count INT NOT NULL DEFAULT 0,
    approved BOOLEAN NOT NULL DEFAULT TRUE,

    CHECK (upvote_count >= 0),
    CHECK (reported_count >= 0),

    CHECK (length(trim(text)) > 0),

    CHECK (created_at <= updated_at),
    CHECK (deleted_at IS NULL OR deleted_at >= created_at),

    CHECK (parent_discussion_comment_id IS NULL OR parent_discussion_comment_id <> id),

    UNIQUE (id, discussion_id),

    FOREIGN KEY (parent_discussion_comment_id, discussion_id)
    REFERENCES discussion_comment(id, discussion_id)
    ON DELETE SET NULL
);
CREATE INDEX idx_discussion_comment_discussion ON discussion_comment (discussion_id);
CREATE INDEX idx_discussion_comment_account ON discussion_comment (account_id);

CREATE TABLE user_discussion_pin (
    id BIGSERIAL PRIMARY KEY,
    discussion_id BIGINT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (discussion_id, account_id)
);
CREATE INDEX idx_user_discussion_pin_discussion ON user_discussion_pin (discussion_id);
CREATE INDEX idx_user_discussion_pin_account ON user_discussion_pin (account_id);





-- ===========================================================================
-- Upvotes, reports, notifications
-- ===========================================================================

CREATE TABLE review_upvote (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, review_id)
);
CREATE INDEX idx_review_upvote_account ON review_upvote (account_id);
CREATE INDEX idx_review_upvote_review ON review_upvote (review_id);

CREATE TABLE question_upvote (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, question_id)
);
CREATE INDEX idx_question_upvote_account ON question_upvote (account_id);
CREATE INDEX idx_question_upvote_question ON question_upvote (question_id);

CREATE TABLE answer_upvote (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    answer_id BIGINT NOT NULL REFERENCES answer(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, answer_id)
);
CREATE INDEX idx_answer_upvote_account ON answer_upvote (account_id);
CREATE INDEX idx_answer_upvote_answer ON answer_upvote (answer_id);

CREATE TABLE discussion_upvote (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_id BIGINT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, discussion_id)
);
CREATE INDEX idx_discussion_upvote_account ON discussion_upvote (account_id);
CREATE INDEX idx_discussion_upvote_discussion ON discussion_upvote (discussion_id);

CREATE TABLE discussion_comment_upvote (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_comment_id BIGINT NOT NULL REFERENCES discussion_comment(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (account_id, discussion_comment_id)
);
CREATE INDEX idx_discussion_comment_upvote_account ON discussion_comment_upvote (account_id);
CREATE INDEX idx_discussion_comment_upvote_discussion_comment ON discussion_comment_upvote (discussion_comment_id);

------------------------------------------------------------------------------

CREATE TYPE report_status_enum AS ENUM ('OPEN', 'REVIEWING', 'RESOLVED', 'REJECTED');

CREATE TABLE review_report (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL REFERENCES review(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,

    UNIQUE (account_id, review_id)
);
CREATE INDEX idx_review_report_account ON review_report (account_id);
CREATE INDEX idx_review_report_review ON review_report (review_id);
CREATE INDEX idx_review_report_status ON review_report (status);

CREATE TABLE question_report (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,

    UNIQUE (account_id, question_id)
);
CREATE INDEX idx_question_report_account ON question_report (account_id);
CREATE INDEX idx_question_report_question ON question_report (question_id);
CREATE INDEX idx_question_report_status ON question_report (status);

CREATE TABLE answer_report (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    answer_id BIGINT NOT NULL REFERENCES answer(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,

    UNIQUE (account_id, answer_id)
);
CREATE INDEX idx_answer_report_account ON answer_report (account_id);
CREATE INDEX idx_answer_report_answer ON answer_report (answer_id);
CREATE INDEX idx_answer_report_status ON answer_report (status);

CREATE TABLE discussion_report (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_id BIGINT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,

    UNIQUE (account_id, discussion_id)
);
CREATE INDEX idx_discussion_report_account ON discussion_report (account_id);
CREATE INDEX idx_discussion_report_discussion ON discussion_report (discussion_id);
CREATE INDEX idx_discussion_report_status ON discussion_report (status);

CREATE TABLE discussion_comment_report (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    discussion_comment_id BIGINT NOT NULL REFERENCES discussion_comment(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,

    UNIQUE (account_id, discussion_comment_id)
);
CREATE INDEX idx_discussion_comment_report_account ON discussion_comment_report (account_id);
CREATE INDEX idx_discussion_comment_report_discussion_comment ON discussion_comment_report (discussion_comment_id);
CREATE INDEX idx_discussion_comment_report_status ON discussion_comment_report (status);

CREATE TABLE product_report (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    reason TEXT,
    status report_status_enum NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMPTZ DEFAULT NULL,

    UNIQUE (account_id, product_id)
);
CREATE INDEX idx_product_report_account ON product_report (account_id);
CREATE INDEX idx_product_report_product ON product_report (product_id);
CREATE INDEX idx_product_report_status ON product_report (status);

------------------------------------------------------------------------------
CREATE TYPE notification_type_enum AS ENUM (
    'PRODUCT_QUESTION',
    'QUESTION_ANSWERED',
    'DISCUSSION_COMMENTED',
    'DISCUSSION_COMMENT_COMMENTED',
    'REVIEW_UPVOTED',
    'QUESTION_UPVOTED',
    'ANSWER_UPVOTED',
    'DISCUSSION_UPVOTED',
    'DISCUSSION_COMMENT_UPVOTED'
);

CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    actor_id BIGINT NULL REFERENCES account(id) ON DELETE SET NULL,
    type notification_type_enum NOT NULL,
    read_at TIMESTAMPTZ NULL, --null is unread
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,

    CHECK (jsonb_typeof(payload) = 'object')
);
CREATE INDEX idx_notification_recipient ON notification (recipient_id);
CREATE INDEX idx_notification_actor ON notification (actor_id);
CREATE INDEX idx_notification_type ON notification (type);

CREATE TABLE product_question_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);
CREATE INDEX idx_product_question_notification_product ON product_question_notification (product_id);
CREATE INDEX idx_product_question_notification_question ON product_question_notification (question_id);

CREATE TABLE question_answered_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    answer_id BIGINT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_question_answered_notification_question ON question_answered_notification (question_id);
CREATE INDEX idx_question_answered_notification_answer ON question_answered_notification (answer_id);

CREATE TABLE discussion_comment_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_id BIGINT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE,
    discussion_comment_id BIGINT NOT NULL REFERENCES discussion_comment(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_comment_notification_discussion ON discussion_comment_notification (discussion_id);
CREATE INDEX idx_discussion_comment_notification_discussion_comment ON discussion_comment_notification (discussion_comment_id);

CREATE TABLE discussion_comment_comment_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    parent_discussion_comment_id BIGINT NOT NULL REFERENCES discussion_comment(id) ON DELETE CASCADE,
    discussion_comment_id BIGINT NOT NULL REFERENCES discussion_comment(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_comment_comment_notification_parent_discussion_comment ON discussion_comment_comment_notification (parent_discussion_comment_id);
CREATE INDEX idx_discussion_comment_comment_notification_discussion_comment ON discussion_comment_comment_notification (discussion_comment_id);

CREATE TABLE review_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL REFERENCES review(id) ON DELETE CASCADE
);
CREATE INDEX idx_review_upvoted_notification_review ON review_upvoted_notification (review_id);

CREATE TABLE question_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE
);
CREATE INDEX idx_question_upvoted_notification_question ON question_upvoted_notification (question_id);

CREATE TABLE answer_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    answer_id BIGINT NOT NULL REFERENCES answer(id) ON DELETE CASCADE
);
CREATE INDEX idx_answer_upvoted_notification_answer ON answer_upvoted_notification (answer_id);

CREATE TABLE discussion_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_id BIGINT NOT NULL REFERENCES discussion(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_upvoted_notification_discussion ON discussion_upvoted_notification (discussion_id);

CREATE TABLE discussion_comment_upvoted_notification (
    notification_id BIGINT PRIMARY KEY REFERENCES notification(id) ON DELETE CASCADE,
    discussion_comment_id BIGINT NOT NULL REFERENCES discussion_comment(id) ON DELETE CASCADE
);
CREATE INDEX idx_discussion_comment_upvoted_notification_discussion_comment ON discussion_comment_upvoted_notification (discussion_comment_id);



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
    id BIGSERIAL PRIMARY KEY,
    actor_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    type activity_type_enum NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,

    CHECK (jsonb_typeof(payload) = 'object')
);
CREATE INDEX idx_activity_actor ON activity (actor_id);
CREATE INDEX idx_activity_type ON activity (type);

CREATE TABLE review_activity (
    activity_id BIGINT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL REFERENCES review(id) ON DELETE CASCADE
);
CREATE INDEX idx_review_activity_review ON review_activity (review_id);

CREATE TABLE routine_item_activity (
    activity_id BIGINT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    routine_item_id BIGINT NOT NULL REFERENCES routine_item(id) ON DELETE CASCADE
);
CREATE INDEX idx_routine_item_activity_routine_item ON routine_item_activity (routine_item_id);

CREATE TABLE routine_image_activity (
    activity_id BIGINT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    routine_image_id BIGINT NOT NULL REFERENCES routine_image(id) ON DELETE CASCADE
);
CREATE INDEX idx_routine_image_activity_routine_image ON routine_image_activity (routine_image_id);

CREATE TABLE wishlist_item_activity (
    activity_id BIGINT PRIMARY KEY REFERENCES activity(id) ON DELETE CASCADE,
    wishlist_item_id BIGINT NOT NULL REFERENCES wishlist_item(id) ON DELETE CASCADE
);
CREATE INDEX idx_wishlist_item_activity_wishlist_item ON wishlist_item_activity (wishlist_item_id);

-- ===========================================================================
-- Breakout List
-- ===========================================================================

CREATE TABLE breakout_list (
    account_id BIGINT PRIMARY KEY NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE breakout_list_ingredient (
    id BIGSERIAL PRIMARY KEY,
    breakout_list_id BIGINT NOT NULL REFERENCES breakout_list(account_id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (breakout_list_id, ingredient_id)
);
CREATE INDEX idx_breakout_list_ingredient_breakout_list ON breakout_list_ingredient (breakout_list_id);
CREATE INDEX idx_breakout_list_ingredient_ingredient ON breakout_list_ingredient (ingredient_id);

CREATE TABLE breakout_list_product (
    id BIGSERIAL PRIMARY KEY,
    breakout_list_id BIGINT NOT NULL REFERENCES breakout_list(account_id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    UNIQUE (breakout_list_id, product_id)
);
CREATE INDEX idx_breakout_list_product_breakout_list ON breakout_list_product (breakout_list_id);
CREATE INDEX idx_breakout_list_product_product ON breakout_list_product (product_id);
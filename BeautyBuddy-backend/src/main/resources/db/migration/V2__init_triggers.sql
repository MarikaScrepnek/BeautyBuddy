-- ================================================================
-- TRIGGER FUNCTIONS & TRIGGERS
-- ================================================================

-- ================================================================
-- Auto-update updated_at timestamp
-- ================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_account_updated_at_on_profile_change()
RETURNS TRIGGER AS $$
BEGIN
  IF (NEW.username IS DISTINCT FROM OLD.username)
     OR (NEW.email IS DISTINCT FROM OLD.email)
     OR (NEW.password_hash IS DISTINCT FROM OLD.password_hash)
     OR (NEW.display_name IS DISTINCT FROM OLD.display_name)
     OR (NEW.avatar_link IS DISTINCT FROM OLD.avatar_link)
  THEN
    NEW.updated_at := NOW();
  ELSE
    NEW.updated_at := OLD.updated_at;
  END IF;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to all tables with updated_at
CREATE TRIGGER trigger_account_updated_at_profile_change
    BEFORE UPDATE ON account
    FOR EACH ROW EXECUTE FUNCTION set_account_updated_at_on_profile_change();

CREATE TRIGGER trigger_update_category_updated_at
    BEFORE UPDATE ON category
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_brand_updated_at
    BEFORE UPDATE ON brand
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_product_shade_updated_at
    BEFORE UPDATE ON product_shade
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_ingredient_updated_at
    BEFORE UPDATE ON ingredient
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_product_updated_at
    BEFORE UPDATE ON product
    FOR EACH ROW
    WHEN (
        NEW.name IS DISTINCT FROM OLD.name OR
        NEW.brand_id IS DISTINCT FROM OLD.brand_id OR
        NEW.category_id IS DISTINCT FROM OLD.category_id OR
        NEW.price IS DISTINCT FROM OLD.price OR
        NEW.image_link IS DISTINCT FROM OLD.image_link OR
        NEW.product_link IS DISTINCT FROM OLD.product_link OR
        NEW.raw_ingredients IS DISTINCT FROM OLD.raw_ingredients OR
        NEW.may_contain_raw_ingredients IS DISTINCT FROM OLD.may_contain_raw_ingredients OR
        NEW.is_discontinued IS DISTINCT FROM OLD.is_discontinued
    )
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_wishlist_updated_at
    BEFORE UPDATE ON wishlist
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_routine_updated_at
    BEFORE UPDATE ON routine
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_review_updated_at
    BEFORE UPDATE ON review
    FOR EACH ROW
    WHEN (
        NEW.account_id IS DISTINCT FROM OLD.account_id OR
        NEW.product_id IS DISTINCT FROM OLD.product_id OR
        NEW.product_shade_id IS DISTINCT FROM OLD.product_shade_id OR
        NEW.rating IS DISTINCT FROM OLD.rating OR
        NEW.title IS DISTINCT FROM OLD.title OR
        NEW.text IS DISTINCT FROM OLD.text OR
        NEW.deleted_at IS DISTINCT FROM OLD.deleted_at OR
        NEW.approved IS DISTINCT FROM OLD.approved
    )
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_question_updated_at
    BEFORE UPDATE ON question
    FOR EACH ROW
    WHEN (
        NEW.account_id IS DISTINCT FROM OLD.account_id OR
        NEW.product_id IS DISTINCT FROM OLD.product_id OR
        NEW.text IS DISTINCT FROM OLD.text OR
        NEW.deleted_at IS DISTINCT FROM OLD.deleted_at OR
        NEW.approved IS DISTINCT FROM OLD.approved
    )
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_answer_updated_at
    BEFORE UPDATE ON answer
    FOR EACH ROW
    WHEN (
        NEW.question_id IS DISTINCT FROM OLD.question_id OR
        NEW.account_id IS DISTINCT FROM OLD.account_id OR
        NEW.text IS DISTINCT FROM OLD.text OR
        NEW.deleted_at IS DISTINCT FROM OLD.deleted_at OR
        NEW.approved IS DISTINCT FROM OLD.approved
    )
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_discussion_updated_at
    BEFORE UPDATE ON discussion
    FOR EACH ROW
    WHEN (
        NEW.account_id IS DISTINCT FROM OLD.account_id OR
        NEW.title IS DISTINCT FROM OLD.title OR
        NEW.text IS DISTINCT FROM OLD.text OR
        NEW.deleted_at IS DISTINCT FROM OLD.deleted_at OR
        NEW.approved IS DISTINCT FROM OLD.approved
    )
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_discussion_comment_updated_at
    BEFORE UPDATE ON discussion_comment
    FOR EACH ROW
    WHEN (
        NEW.discussion_id IS DISTINCT FROM OLD.discussion_id OR
        NEW.account_id IS DISTINCT FROM OLD.account_id OR
        NEW.parent_discussion_comment_id IS DISTINCT FROM OLD.parent_discussion_comment_id OR
        NEW.text IS DISTINCT FROM OLD.text OR
        NEW.deleted_at IS DISTINCT FROM OLD.deleted_at OR
        NEW.approved IS DISTINCT FROM OLD.approved
    )
    EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION update_product_purchase_on_repurchase()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'UPDATE' THEN
        NEW.last_purchased_at = NOW();
        NEW.times_purchased = OLD.times_purchased + 1;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_product_purchase
    BEFORE UPDATE ON product_purchase
    FOR EACH ROW EXECUTE FUNCTION update_product_purchase_on_repurchase();


-- ================================================================
-- Maintain follower/following counts
-- ================================================================

CREATE OR REPLACE FUNCTION update_follow_counts()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- Increment following_count for follower
        UPDATE account 
        SET following_count = following_count + 1 
        WHERE id = NEW.follower_id;
        
        -- Increment followers_count for followed user
        UPDATE account 
        SET followers_count = followers_count + 1 
        WHERE id = NEW.following_id;
        
    ELSIF TG_OP = 'DELETE' THEN
        -- Decrement following_count for follower
        UPDATE account 
        SET following_count = GREATEST(0, following_count - 1)
        WHERE id = OLD.follower_id;
        
        -- Decrement followers_count for followed user
        UPDATE account 
        SET followers_count = GREATEST(0, followers_count - 1)
        WHERE id = OLD.following_id;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_follow_counts
    AFTER INSERT OR DELETE ON account_follow
    FOR EACH ROW EXECUTE FUNCTION update_follow_counts();


-- ================================================================
-- Maintain product review_count and rating
-- ================================================================

CREATE OR REPLACE FUNCTION update_product_review_stats()
RETURNS TRIGGER AS $$
DECLARE
    v_product_id INT;
BEGIN
    -- Get the product_id (works for INSERT, UPDATE, DELETE)
    v_product_id := COALESCE(NEW.product_id, OLD.product_id);
    
    -- Update review_count and average rating
    UPDATE product
    SET 
        review_count = (
            SELECT COUNT(*) 
            FROM review 
            WHERE product_id = v_product_id 
            AND deleted_at IS NULL
        ),
        rating = (
            SELECT ROUND(AVG(rating)::numeric, 1)
            FROM review 
            WHERE product_id = v_product_id 
            AND deleted_at IS NULL 
            AND rating IS NOT NULL
        )
    WHERE id = v_product_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_product_review_stats
    AFTER INSERT OR UPDATE OR DELETE ON review
    FOR EACH ROW EXECUTE FUNCTION update_product_review_stats();


-- ================================================================
-- Maintain review upvote_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_review_upvote_count()
RETURNS TRIGGER AS $$
DECLARE
    v_review_id INT;
BEGIN
    v_review_id := COALESCE(NEW.review_id, OLD.review_id);
    
    UPDATE review
    SET upvote_count = (
        SELECT COUNT(*) 
        FROM review_upvote 
        WHERE review_id = v_review_id
    )
    WHERE id = v_review_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_review_upvote_count
    AFTER INSERT OR DELETE ON review_upvote
    FOR EACH ROW EXECUTE FUNCTION update_review_upvote_count();


-- ================================================================
-- Maintain question upvote_count, answer_count, and answered status
-- ================================================================

CREATE OR REPLACE FUNCTION update_question_upvote_count()
RETURNS TRIGGER AS $$
DECLARE
    v_question_id INT;
BEGIN
    v_question_id := COALESCE(NEW.question_id, OLD.question_id);
    
    UPDATE question
    SET upvote_count = (
        SELECT COUNT(*) 
        FROM question_upvote 
        WHERE question_id = v_question_id
    )
    WHERE id = v_question_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_question_upvote_count
    AFTER INSERT OR DELETE ON question_upvote
    FOR EACH ROW EXECUTE FUNCTION update_question_upvote_count();

CREATE OR REPLACE FUNCTION update_question_answer_stats()
RETURNS TRIGGER AS $$
DECLARE
    v_question_id INT;
    v_answer_exists BOOLEAN;
BEGIN
    v_question_id := COALESCE(NEW.question_id, OLD.question_id);
    
    -- Check if any non-deleted answers exist
    SELECT EXISTS(
        SELECT 1 
        FROM answer 
        WHERE question_id = v_question_id 
        AND deleted_at IS NULL
    ) INTO v_answer_exists;
    
    UPDATE question
    SET 
        answer_count = (
            SELECT COUNT(*) 
            FROM answer 
            WHERE question_id = v_question_id 
            AND deleted_at IS NULL
        ),
        is_answered = v_answer_exists
    WHERE id = v_question_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_question_answer_stats
    AFTER INSERT OR UPDATE OR DELETE ON answer
    FOR EACH ROW EXECUTE FUNCTION update_question_answer_stats();


-- ================================================================
-- Maintain answer upvote_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_answer_upvote_count()
RETURNS TRIGGER AS $$
DECLARE
    v_answer_id INT;
BEGIN
    v_answer_id := COALESCE(NEW.answer_id, OLD.answer_id);
    
    UPDATE answer
    SET upvote_count = (
        SELECT COUNT(*) 
        FROM answer_upvote 
        WHERE answer_id = v_answer_id
    )
    WHERE id = v_answer_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_answer_upvote_count
    AFTER INSERT OR DELETE ON answer_upvote
    FOR EACH ROW EXECUTE FUNCTION update_answer_upvote_count();


-- ================================================================
-- Maintain discussion upvote_count and reply_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_discussion_upvote_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_id INT;
BEGIN
    v_discussion_id := COALESCE(NEW.discussion_id, OLD.discussion_id);
    
    UPDATE discussion
    SET upvote_count = (
        SELECT COUNT(*) 
        FROM discussion_upvote 
        WHERE discussion_id = v_discussion_id
    )
    WHERE id = v_discussion_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_upvote_count
    AFTER INSERT OR DELETE ON discussion_upvote
    FOR EACH ROW EXECUTE FUNCTION update_discussion_upvote_count();

CREATE OR REPLACE FUNCTION update_discussion_reply_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_id INT;
BEGIN
    v_discussion_id := COALESCE(NEW.discussion_id, OLD.discussion_id);
    
    UPDATE discussion
    SET reply_count = (
        SELECT COUNT(*) 
        FROM discussion_comment 
        WHERE discussion_id = v_discussion_id 
        AND deleted_at IS NULL
    )
    WHERE id = v_discussion_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_reply_count
    AFTER INSERT OR UPDATE OR DELETE ON discussion_comment
    FOR EACH ROW EXECUTE FUNCTION update_discussion_reply_count();


-- ================================================================
-- Maintain discussion_comment upvote_count and reply_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_discussion_comment_upvote_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_comment_id INT;
BEGIN
    v_discussion_comment_id := COALESCE(NEW.discussion_comment_id, OLD.discussion_comment_id);
    
    UPDATE discussion_comment
    SET upvote_count = (
        SELECT COUNT(*) 
        FROM discussion_comment_upvote 
        WHERE discussion_comment_id = v_discussion_comment_id
    )
    WHERE id = v_discussion_comment_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_comment_upvote_count
    AFTER INSERT OR DELETE ON discussion_comment_upvote
    FOR EACH ROW EXECUTE FUNCTION update_discussion_comment_upvote_count();

CREATE OR REPLACE FUNCTION update_discussion_comment_reply_count()
RETURNS TRIGGER AS $$
DECLARE
    v_parent_comment_id INT;
BEGIN
    v_parent_comment_id := COALESCE(NEW.parent_discussion_comment_id, OLD.parent_discussion_comment_id);
    
    IF v_parent_comment_id IS NOT NULL THEN
        UPDATE discussion_comment
        SET reply_count = (
            SELECT COUNT(*) 
            FROM discussion_comment 
            WHERE parent_discussion_comment_id = v_parent_comment_id 
            AND deleted_at IS NULL
        )
        WHERE id = v_parent_comment_id;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_comment_reply_count
    AFTER INSERT OR UPDATE OR DELETE ON discussion_comment
    FOR EACH ROW EXECUTE FUNCTION update_discussion_comment_reply_count();


-- ================================================================
-- Update wishlist.updated_at when items change
-- ================================================================

CREATE OR REPLACE FUNCTION update_wishlist_on_item_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE wishlist 
    SET updated_at = NOW() 
    WHERE account_id = COALESCE(NEW.wishlist_id, OLD.wishlist_id);
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_wishlist_on_item_change
    AFTER INSERT OR UPDATE OR DELETE ON wishlist_item
    FOR EACH ROW EXECUTE FUNCTION update_wishlist_on_item_change();


-- ================================================================
-- Update routine.updated_at when items or images change
-- ================================================================

/* CREATE OR REPLACE FUNCTION update_routine_on_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE routine 
    SET updated_at = NOW() 
    WHERE id = COALESCE(NEW.routine_id, OLD.routine_id);
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_routine_on_item_change
    AFTER INSERT OR UPDATE OR DELETE ON routine_item
    FOR EACH ROW EXECUTE FUNCTION update_routine_on_change();

CREATE TRIGGER trigger_update_routine_on_image_change
    AFTER INSERT OR UPDATE OR DELETE ON routine_image
    FOR EACH ROW EXECUTE FUNCTION update_routine_on_change(); */


-- ================================================================
-- Maintain reported_count on text
-- ================================================================

CREATE OR REPLACE FUNCTION update_review_reported_count()
RETURNS TRIGGER AS $$
DECLARE
    v_review_id INT;
BEGIN
    v_review_id := COALESCE(NEW.review_id, OLD.review_id);
    
    UPDATE review
    SET reported_count = (
        SELECT COUNT(*) 
        FROM review_report 
        WHERE review_id = v_review_id
    )
    WHERE id = v_review_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_review_reported_count
    AFTER INSERT OR DELETE ON review_report
    FOR EACH ROW EXECUTE FUNCTION update_review_reported_count();

CREATE OR REPLACE FUNCTION update_question_reported_count()
RETURNS TRIGGER AS $$
DECLARE
    v_question_id INT;
BEGIN
    v_question_id := COALESCE(NEW.question_id, OLD.question_id);
    
    UPDATE question
    SET reported_count = (
        SELECT COUNT(*) 
        FROM question_report 
        WHERE question_id = v_question_id
    )
    WHERE id = v_question_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_question_reported_count
    AFTER INSERT OR DELETE ON question_report
    FOR EACH ROW EXECUTE FUNCTION update_question_reported_count();

CREATE OR REPLACE FUNCTION update_answer_reported_count()
RETURNS TRIGGER AS $$
DECLARE
    v_answer_id INT;
BEGIN
    v_answer_id := COALESCE(NEW.answer_id, OLD.answer_id);
    
    UPDATE answer
    SET reported_count = (
        SELECT COUNT(*) 
        FROM answer_report 
        WHERE answer_id = v_answer_id
    )
    WHERE id = v_answer_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_answer_reported_count
    AFTER INSERT OR DELETE ON answer_report
    FOR EACH ROW EXECUTE FUNCTION update_answer_reported_count();

CREATE OR REPLACE FUNCTION update_discussion_reported_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_id INT;
BEGIN
    v_discussion_id := COALESCE(NEW.discussion_id, OLD.discussion_id);
    
    UPDATE discussion
    SET reported_count = (
        SELECT COUNT(*) 
        FROM discussion_report 
        WHERE discussion_id = v_discussion_id
    )
    WHERE id = v_discussion_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_reported_count
    AFTER INSERT OR DELETE ON discussion_report
    FOR EACH ROW EXECUTE FUNCTION update_discussion_reported_count();

CREATE OR REPLACE FUNCTION update_discussion_comment_reported_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_comment_id INT;
BEGIN
    v_discussion_comment_id := COALESCE(NEW.discussion_comment_id, OLD.discussion_comment_id);
    
    UPDATE discussion_comment
    SET reported_count = (
        SELECT COUNT(*) 
        FROM discussion_comment_report 
        WHERE discussion_comment_id = v_discussion_comment_id
    )
    WHERE id = v_discussion_comment_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trigger_update_discussion_comment_reported_count
    AFTER INSERT OR DELETE ON discussion_comment_report
    FOR EACH ROW EXECUTE FUNCTION update_discussion_comment_reported_count();

CREATE OR REPLACE FUNCTION update_unread_notifications_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- New notification, increment count
        UPDATE account 
        SET unread_notifications_count = unread_notifications_count + 1
        WHERE id = NEW.recipient_id;
        
    ELSIF TG_OP = 'UPDATE' AND OLD.read_at IS NULL AND NEW.read_at IS NOT NULL THEN
        -- Notification was marked as read, decrement count
        UPDATE account 
        SET unread_notifications_count = GREATEST(0, unread_notifications_count - 1)
        WHERE id = NEW.recipient_id;
        
    ELSIF TG_OP = 'DELETE' AND OLD.read_at IS NULL THEN
        -- Unread notification deleted, decrement count
        UPDATE account 
        SET unread_notifications_count = GREATEST(0, unread_notifications_count - 1)
        WHERE id = OLD.recipient_id;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_unread_notifications_count
    AFTER INSERT OR UPDATE OR DELETE ON notification
    FOR EACH ROW EXECUTE FUNCTION update_unread_notifications_count();


CREATE OR REPLACE FUNCTION create_wishlist_on_first_item()
RETURNS TRIGGER AS $$
BEGIN
    -- Ensure wishlist row exists
    INSERT INTO wishlist (account_id, updated_at)
    VALUES (NEW.wishlist_id, NOW())
    ON CONFLICT (account_id) DO NOTHING;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_create_wishlist_on_first_item
    BEFORE INSERT ON wishlist_item
    FOR EACH ROW EXECUTE FUNCTION create_wishlist_on_first_item();
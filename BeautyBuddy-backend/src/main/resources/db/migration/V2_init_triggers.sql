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

-- Apply to all tables with updated_at
CREATE TRIGGER trigger_update_category_updated_at
    BEFORE UPDATE ON category
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_brand_updated_at
    BEFORE UPDATE ON brand
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_product_updated_at
    BEFORE UPDATE ON product
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_wishlist_updated_at
    BEFORE UPDATE ON wishlist
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_routine_updated_at
    BEFORE UPDATE ON routine
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_review_updated_at
    BEFORE UPDATE ON review
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_question_updated_at
    BEFORE UPDATE ON question
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_answer_updated_at
    BEFORE UPDATE ON answer
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_discussion_updated_at
    BEFORE UPDATE ON discussion
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_discussion_answer_updated_at
    BEFORE UPDATE ON discussion_answer
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_update_public_community_post_updated_at
    BEFORE UPDATE ON public_community_post
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


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
    -- Get product_id from either NEW or OLD
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
-- Maintain review helpful_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_review_helpful_count()
RETURNS TRIGGER AS $$
DECLARE
    v_review_id INT;
BEGIN
    v_review_id := COALESCE(NEW.review_id, OLD.review_id);
    
    UPDATE review
    SET helpful_count = (
        SELECT COUNT(*) 
        FROM review_upvote 
        WHERE review_id = v_review_id
    )
    WHERE id = v_review_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_review_helpful_count
    AFTER INSERT OR DELETE ON review_upvote
    FOR EACH ROW EXECUTE FUNCTION update_review_helpful_count();


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
        answered = v_answer_exists
    WHERE id = v_question_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_question_answer_stats
    AFTER INSERT OR UPDATE OR DELETE ON answer
    FOR EACH ROW EXECUTE FUNCTION update_question_answer_stats();


-- ================================================================
-- Maintain answer helpful_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_answer_helpful_count()
RETURNS TRIGGER AS $$
DECLARE
    v_answer_id INT;
BEGIN
    v_answer_id := COALESCE(NEW.answer_id, OLD.answer_id);
    
    UPDATE answer
    SET helpful_count = (
        SELECT COUNT(*) 
        FROM answer_upvote 
        WHERE answer_id = v_answer_id
    )
    WHERE id = v_answer_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_answer_helpful_count
    AFTER INSERT OR DELETE ON answer_upvote
    FOR EACH ROW EXECUTE FUNCTION update_answer_helpful_count();


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
        FROM discussion_answer 
        WHERE discussion_id = v_discussion_id 
        AND deleted_at IS NULL
    )
    WHERE id = v_discussion_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_reply_count
    AFTER INSERT OR UPDATE OR DELETE ON discussion_answer
    FOR EACH ROW EXECUTE FUNCTION update_discussion_reply_count();


-- ================================================================
-- Maintain discussion_answer helpful_count
-- ================================================================

CREATE OR REPLACE FUNCTION update_discussion_answer_helpful_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_answer_id INT;
BEGIN
    v_discussion_answer_id := COALESCE(NEW.discussion_answer_id, OLD.discussion_answer_id);
    
    UPDATE discussion_answer
    SET helpful_count = (
        SELECT COUNT(*) 
        FROM discussion_answer_upvote 
        WHERE discussion_answer_id = v_discussion_answer_id
    )
    WHERE id = v_discussion_answer_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_answer_helpful_count
    AFTER INSERT OR DELETE ON discussion_answer_upvote
    FOR EACH ROW EXECUTE FUNCTION update_discussion_answer_helpful_count();


-- ================================================================
-- Update wishlist.updated_at when items change
-- ================================================================

CREATE OR REPLACE FUNCTION update_wishlist_on_item_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE wishlist 
    SET updated_at = NOW() 
    WHERE id = COALESCE(NEW.wishlist_id, OLD.wishlist_id);
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_wishlist_on_item_change
    AFTER INSERT OR UPDATE OR DELETE ON wishlist_item
    FOR EACH ROW EXECUTE FUNCTION update_wishlist_on_item_change();


-- ================================================================
-- Update routine.updated_at when items or images change
-- ================================================================

CREATE OR REPLACE FUNCTION update_routine_on_change()
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
    FOR EACH ROW EXECUTE FUNCTION update_routine_on_change();


-- ================================================================
-- Maintain reported_count on content
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

CREATE OR REPLACE FUNCTION update_discussion_answer_reported_count()
RETURNS TRIGGER AS $$
DECLARE
    v_discussion_answer_id INT;
BEGIN
    v_discussion_answer_id := COALESCE(NEW.discussion_answer_id, OLD.discussion_answer_id);
    
    UPDATE discussion_answer
    SET reported_count = (
        SELECT COUNT(*) 
        FROM discussion_answer_report 
        WHERE discussion_answer_id = v_discussion_answer_id
    )
    WHERE id = v_discussion_answer_id;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_answer_reported_count
    AFTER INSERT OR DELETE ON discussion_answer_report
    FOR EACH ROW EXECUTE FUNCTION update_discussion_answer_reported_count();
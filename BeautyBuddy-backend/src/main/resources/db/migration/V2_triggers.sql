-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply to relevant tables
CREATE TRIGGER update_category_updated_at BEFORE UPDATE ON category
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_brand_updated_at BEFORE UPDATE ON brand
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_product_updated_at BEFORE UPDATE ON product
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_wishlist_updated_at BEFORE UPDATE ON wishlist
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_routine_updated_at BEFORE UPDATE ON routine
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_review_updated_at BEFORE UPDATE ON review
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_question_updated_at BEFORE UPDATE ON question
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_answer_updated_at BEFORE UPDATE ON answer
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_discussion_updated_at BEFORE UPDATE ON discussion
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_discussion_answer_updated_at BEFORE UPDATE ON discussion_answer
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================

-- Function to update followers_count
CREATE OR REPLACE FUNCTION update_followers_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE account SET followers_count = followers_count + 1 
        WHERE id = NEW.following_id;
        UPDATE account SET following_count = following_count + 1 
        WHERE id = NEW.follower_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE account SET followers_count = GREATEST(0, followers_count - 1) 
        WHERE id = OLD.following_id;
        UPDATE account SET following_count = GREATEST(0, following_count - 1) 
        WHERE id = OLD.follower_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_followers_count
AFTER INSERT OR DELETE ON user_follow
FOR EACH ROW EXECUTE FUNCTION update_followers_count();

-- ============================================================================

-- Function to update question answered status
CREATE OR REPLACE FUNCTION update_question_answered()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.deleted_at IS NULL THEN
        UPDATE question 
        SET answered = TRUE, 
            answer_count = answer_count + 1
        WHERE id = NEW.question_id;
    ELSIF TG_OP = 'DELETE' OR (TG_OP = 'UPDATE' AND NEW.deleted_at IS NOT NULL AND OLD.deleted_at IS NULL) THEN
        UPDATE question 
        SET answer_count = GREATEST(0, answer_count - 1),
            answered = EXISTS(
                SELECT 1 FROM answer 
                WHERE question_id = COALESCE(NEW.question_id, OLD.question_id) 
                AND deleted_at IS NULL
                AND id != COALESCE(NEW.id, OLD.id)
            )
        WHERE id = COALESCE(NEW.question_id, OLD.question_id);
    ELSIF TG_OP = 'UPDATE' AND NEW.deleted_at IS NULL AND OLD.deleted_at IS NOT NULL THEN
        UPDATE question
        SET answer_count = answer_count + 1,
            answered = TRUE
        WHERE id = NEW.question_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_question_answered
AFTER INSERT OR UPDATE OR DELETE ON answer
FOR EACH ROW EXECUTE FUNCTION update_question_answered();

-- ============================================================================

-- Function to update review counts on product
CREATE OR REPLACE FUNCTION update_product_review_stats()
RETURNS TRIGGER AS $$
DECLARE
    v_product_id INT;
    v_old_product_id INT;
BEGIN
    v_product_id := COALESCE(NEW.product_id, OLD.product_id);
    v_old_product_id := OLD.product_id;
    
    UPDATE product
    SET 
        review_count = (
            SELECT COUNT(*) FROM review 
            WHERE product_id = v_product_id AND deleted_at IS NULL
        ),
        rating = (
            SELECT ROUND(AVG(rating)::numeric, 2) FROM review 
            WHERE product_id = v_product_id AND deleted_at IS NULL AND rating IS NOT NULL
        )
    WHERE id = v_product_id;

    IF TG_OP = 'UPDATE' AND v_old_product_id IS NOT NULL AND v_old_product_id <> v_product_id THEN
        UPDATE product
        SET 
            review_count = (
                SELECT COUNT(*) FROM review 
                WHERE product_id = v_old_product_id AND deleted_at IS NULL
            ),
            rating = (
                SELECT ROUND(AVG(rating)::numeric, 2) FROM review 
                WHERE product_id = v_old_product_id AND deleted_at IS NULL AND rating IS NOT NULL
            )
        WHERE id = v_old_product_id;
    END IF;
    
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_product_review_stats
AFTER INSERT OR UPDATE OR DELETE ON review
FOR EACH ROW EXECUTE FUNCTION update_product_review_stats();

-- ============================================================================

-- Function to update discussion reply count
CREATE OR REPLACE FUNCTION update_discussion_reply_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.deleted_at IS NULL THEN
        UPDATE discussion 
        SET reply_count = reply_count + 1
        WHERE id = NEW.discussion_id;
    ELSIF TG_OP = 'DELETE' OR (TG_OP = 'UPDATE' AND NEW.deleted_at IS NOT NULL AND OLD.deleted_at IS NULL) THEN
        UPDATE discussion 
        SET reply_count = GREATEST(0, reply_count - 1)
        WHERE id = COALESCE(NEW.discussion_id, OLD.discussion_id);
    ELSIF TG_OP = 'UPDATE' AND NEW.deleted_at IS NULL AND OLD.deleted_at IS NOT NULL THEN
        UPDATE discussion
        SET reply_count = reply_count + 1
        WHERE id = NEW.discussion_id;
    END IF;

    IF TG_OP = 'UPDATE' AND NEW.discussion_id <> OLD.discussion_id THEN
        UPDATE discussion
        SET reply_count = GREATEST(0, reply_count - 1)
        WHERE id = OLD.discussion_id;
        UPDATE discussion
        SET reply_count = reply_count + 1
        WHERE id = NEW.discussion_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_discussion_reply_count
AFTER INSERT OR UPDATE OR DELETE ON discussion_answer
FOR EACH ROW EXECUTE FUNCTION update_discussion_reply_count();

-- ============================================================================

-- Function to update wishlist updated_at when items change
CREATE OR REPLACE FUNCTION update_wishlist_on_item_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE wishlist 
    SET updated_at = NOW() 
    WHERE id = COALESCE(NEW.wishlist_id, OLD.wishlist_id);

    IF TG_OP = 'UPDATE' AND NEW.wishlist_id <> OLD.wishlist_id THEN
        UPDATE wishlist
        SET updated_at = NOW()
        WHERE id = OLD.wishlist_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_wishlist_on_item_change
AFTER INSERT OR UPDATE OR DELETE ON wishlist_item
FOR EACH ROW EXECUTE FUNCTION update_wishlist_on_item_change();

-- ============================================================================

-- Function to update routine updated_at when items change
CREATE OR REPLACE FUNCTION update_routine_on_item_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE routine 
    SET updated_at = NOW() 
    WHERE id = COALESCE(NEW.routine_id, OLD.routine_id);

    IF TG_OP = 'UPDATE' AND NEW.routine_id <> OLD.routine_id THEN
        UPDATE routine
        SET updated_at = NOW()
        WHERE id = OLD.routine_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_routine_on_item_change
AFTER INSERT OR UPDATE OR DELETE ON routine_item
FOR EACH ROW EXECUTE FUNCTION update_routine_on_item_change();

-- ============================================================================

-- Enforce routine category is a top-level category
CREATE OR REPLACE FUNCTION validate_routine_category_top_level()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM category
        WHERE id = NEW.category_id
          AND parent_category_id IS NULL
    ) THEN
        RAISE EXCEPTION 'routine.category_id must reference a top-level category';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validate_routine_category_top_level
BEFORE INSERT OR UPDATE ON routine
FOR EACH ROW EXECUTE FUNCTION validate_routine_category_top_level();
-- ================================================================
-- PERFORMANCE INDEXES
-- Partial and compound indexes for common query patterns
-- ================================================================

-- ================================================================
-- Review performance indexes
-- ================================================================

-- Most upvoted reviews (sorted by upvote_count)
CREATE INDEX idx_review_upvote
ON review (product_id, upvote_count DESC) 
WHERE deleted_at IS NULL;

-- Recent reviews
CREATE INDEX idx_review_recent 
ON review (product_id, created_at DESC) 
WHERE deleted_at IS NULL;

-- Active reviews only
CREATE INDEX idx_review_active 
ON review (product_id) 
WHERE deleted_at IS NULL;


-- ================================================================
-- Question performance indexes
-- ================================================================

-- Unanswered questions (commonly needed for product pages)
CREATE INDEX idx_question_unanswered 
ON question (product_id, created_at DESC) 
WHERE is_answered = FALSE AND deleted_at IS NULL;

-- Most upvoted questions
CREATE INDEX idx_question_upvotes 
ON question (product_id, upvote_count DESC) 
WHERE deleted_at IS NULL;

-- Active questions only
CREATE INDEX idx_question_active 
ON question (product_id) 
WHERE deleted_at IS NULL;


-- ================================================================
-- Answer performance indexes
-- ================================================================

-- Active answers only
CREATE INDEX idx_answer_active 
ON answer (question_id) 
WHERE deleted_at IS NULL;

-- Most upvoted answers
CREATE INDEX idx_answer_upvote
ON answer (question_id, upvote_count DESC) 
WHERE deleted_at IS NULL;


-- ================================================================
-- Discussion performance indexes
-- ================================================================

-- Active discussions sorted by recent
CREATE INDEX idx_discussion_recent 
ON discussion (created_at DESC) 
WHERE deleted_at IS NULL;

-- Most upvoted discussions
CREATE INDEX idx_discussion_upvoted 
ON discussion (upvote_count DESC, created_at DESC) 
WHERE deleted_at IS NULL;

-- Active discussions only
CREATE INDEX idx_discussion_active 
ON discussion (deleted_at) 
WHERE deleted_at IS NULL;


-- ================================================================
-- Discussion answer performance indexes
-- ================================================================

-- Active discussion answers
CREATE INDEX idx_discussion_comment_active 
ON discussion_comment (discussion_id, created_at) 
WHERE deleted_at IS NULL;

-- Most upvoted discussion answers
CREATE INDEX idx_discussion_comment_upvote 
ON discussion_comment (discussion_id, upvote_count DESC) 
WHERE deleted_at IS NULL;


-- ================================================================
-- Notification performance indexes
-- ================================================================

-- Unread notifications (most common query)
CREATE INDEX idx_notification_unread 
ON notification (recipient_id, created_at DESC) 
WHERE read_at IS NULL;

-- All notifications by recipient
CREATE INDEX idx_notification_recipient_recent 
ON notification (recipient_id, created_at DESC);


-- ================================================================
-- Report performance indexes (admin dashboard)
-- ================================================================

-- Open review reports
CREATE INDEX idx_review_report_open 
ON review_report (status, created_at DESC) 
WHERE status = 'OPEN';

-- Open question reports
CREATE INDEX idx_question_report_open 
ON question_report (status, created_at DESC) 
WHERE status = 'OPEN';

-- Open answer reports
CREATE INDEX idx_answer_report_open 
ON answer_report (status, created_at DESC) 
WHERE status = 'OPEN';

-- Open discussion reports
CREATE INDEX idx_discussion_report_open 
ON discussion_report (status, created_at DESC) 
WHERE status = 'OPEN';

-- Open discussion answer reports
CREATE INDEX idx_discussion_comment_report_open 
ON discussion_comment_report (status, created_at DESC) 
WHERE status = 'OPEN';


-- ================================================================
-- Activity feed performance indexes
-- ================================================================

-- Activity by actor and time (for user profile feeds)
CREATE INDEX idx_activity_actor_recent 
ON activity (actor_id, created_at DESC);

-- Activity by type (for filtering activity types)
CREATE INDEX idx_activity_type_recent 
ON activity (type, created_at DESC);


-- ================================================================
-- Routine item performance indexes
-- ================================================================

-- Current routine items by product (for "who uses this product?")
CREATE INDEX idx_routine_item_product_current 
ON routine_item (product_id) 
WHERE valid_to IS NULL;

-- Current routine items sorted by step
CREATE INDEX idx_routine_item_current_steps 
ON routine_item (routine_id, step_order) 
WHERE valid_to IS NULL;


-- ================================================================
-- Product performance indexes
-- ================================================================

-- Top rated products
CREATE INDEX idx_product_top_rated 
ON product (rating DESC NULLS LAST, review_count DESC) 
WHERE rating IS NOT NULL;

-- Products by category and rating
CREATE INDEX idx_product_category_rating 
ON product (category_id, rating DESC NULLS LAST);

-- Recently added products
CREATE INDEX idx_product_recent 
ON product (created_at DESC);


-- ================================================================
-- Ingredient filtering indexes
-- ================================================================

-- Common allergen ingredients
CREATE INDEX idx_ingredient_allergen 
ON ingredient (is_common_allergen) 
WHERE is_common_allergen = TRUE;

-- Fragrance ingredients
CREATE INDEX idx_ingredient_fragrance 
ON ingredient (is_fragrance) 
WHERE is_fragrance = TRUE;


-- ================================================================
-- Account performance indexes
-- ================================================================

-- Private accounts
CREATE INDEX idx_account_private 
ON account (is_private) 
WHERE is_private = TRUE;
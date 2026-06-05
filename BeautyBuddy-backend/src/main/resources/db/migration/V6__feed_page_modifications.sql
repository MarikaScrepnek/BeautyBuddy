DROP TABLE review_activity;

DROP TABLE routine_item_activity;

DROP TABLE routine_image_activity;

DROP TABLE wishlist_item_activity;

ALTER TABLE activity
    ADD COLUMN target_id BIGINT;

ALTER TABLE activity
    ALTER COLUMN target_id SET NOT NULL;
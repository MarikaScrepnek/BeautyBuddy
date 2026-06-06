DROP TABLE review_activity;

DROP TABLE routine_item_activity;

DROP TABLE routine_image_activity;

DROP TABLE wishlist_item_activity;

CREATE TYPE activity_action_enum AS ENUM (
    'CREATED',
    'EDITED',
    'ADDED',
    'REMOVED'
);

ALTER TABLE activity
    ADD COLUMN target_id BIGINT NOT NULL;

ALTER TABLE activity
    ADD COLUMN action activity_action_enum NOT NULL;

ALTER TYPE activity_type ADD VALUE 'ROUTINE';

ALTER TYPE activity_type ADD VALUE 'BREAKOUTLIST_ITEM';

ALTER TYPE activity_type ADD VALUE 'BREAKOUTLIST_INGREDIENT';
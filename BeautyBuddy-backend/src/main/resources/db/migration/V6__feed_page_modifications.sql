UPDATE product
    SET image_link = 'https://cdn.shopify.com/s/files/1/0661/2251/4520/files/59910_SCREM_Closed_R_b2f42f49-5465-409f-ac51-557704db3b8b.png?v=1778587286&width=1440&height=1440&crop=center'
    WHERE name = 'Holy Hydration! Makeup Melting Cleansing Balm';

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
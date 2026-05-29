-- V3__fix_follow_trigger.sql

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
        WHERE id = NEW.followed_id;

    ELSIF TG_OP = 'DELETE' THEN
        -- Decrement following_count for follower
        UPDATE account
        SET following_count = GREATEST(0, following_count - 1)
        WHERE id = OLD.follower_id;

        -- Decrement followers_count for followed user
        UPDATE account
        SET followers_count = GREATEST(0, followers_count - 1)
        WHERE id = OLD.followed_id;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
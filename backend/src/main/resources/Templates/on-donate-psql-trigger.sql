CREATE OR REPLACE FUNCTION update_other_donations()
RETURNS TRIGGER AS $$
BEGIN
    -- If the new status is 'ACCEPTED'
    IF NEW.status = 'ACCEPTED' THEN
        -- Update all other donations for the same item to 'REJECTED'
        UPDATE donations 
        SET status = 'REJECTED'
        WHERE item_post_id = NEW.item_post_id
        AND id != NEW.id
        AND status != 'REJECTED';
        
        -- Update the item post status to 'INACTIVE'
        UPDATE item_posts
        SET status = 'INACTIVE'
        WHERE id = NEW.item_post_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE TRIGGER donation_status_update
    AFTER UPDATE OF status
    ON donations
    FOR EACH ROW
    EXECUTE FUNCTION update_other_donations();
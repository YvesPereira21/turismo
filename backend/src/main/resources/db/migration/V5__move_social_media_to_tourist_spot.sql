TRUNCATE TABLE socials_media CASCADE;

ALTER TABLE socials_media DROP CONSTRAINT IF EXISTS fkeaix2r80fxywps99732b13w0l;
-- We'll try to drop the foreign key by doing drop column which often drops the constraint implicitly, but sometimes needs explicit dropping if we know the name. 
-- In PostgreSQL, dropping the column drops the constraint automatically.
ALTER TABLE socials_media DROP COLUMN spot_manager_id;

ALTER TABLE socials_media ADD COLUMN tourist_spot_id UUID;
ALTER TABLE socials_media ADD CONSTRAINT fk_social_media_tourist_spot FOREIGN KEY (tourist_spot_id) REFERENCES tourist_spots(tourist_spot_id);

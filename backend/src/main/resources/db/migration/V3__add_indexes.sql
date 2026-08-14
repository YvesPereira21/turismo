CREATE INDEX idx_tourist_spots_location ON tourist_spots USING GIST (location);
CREATE INDEX idx_cities_name ON cities (name);
CREATE INDEX idx_states_name ON states (name);

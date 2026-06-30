CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    role VARCHAR(50)
);

CREATE TABLE tourists (
    tourist_id UUID PRIMARY KEY,
    birth_date DATE,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE spot_managers (
    spot_manager_id UUID PRIMARY KEY,
    manager_type VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tour_guides (
    tour_guide_id UUID PRIMARY KEY,
    cadastur VARCHAR(255) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE states (
    state_id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE cities (
    city_id UUID PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    state_id UUID NOT NULL REFERENCES states(state_id) ON DELETE CASCADE
);

CREATE TABLE tourist_spots (
    tourist_spot_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location GEOGRAPHY(Point, 4326),
    opens_at TIME,
    closes_at TIME,
    short_description VARCHAR(255),
    description TEXT,
    spot_manager_id UUID NOT NULL REFERENCES spot_managers(spot_manager_id) ON DELETE CASCADE,
    city_id UUID NOT NULL REFERENCES cities(city_id) ON DELETE CASCADE
);

CREATE TABLE socials_media (
    social_media_id UUID PRIMARY KEY,
    social_media_link VARCHAR(255) NOT NULL,
    social_media_type VARCHAR(50) NOT NULL,
    spot_manager_id UUID REFERENCES spot_managers(spot_manager_id) ON DELETE CASCADE
);

CREATE TABLE photos (
    photo_id UUID PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    tourist_spot_id UUID NOT NULL REFERENCES tourist_spots(tourist_spot_id) ON DELETE CASCADE
);

CREATE TABLE activities (
    activity_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tourist_spot_id UUID NOT NULL REFERENCES tourist_spots(tourist_spot_id) ON DELETE CASCADE,
    photo_id UUID NOT NULL UNIQUE REFERENCES photos(photo_id) ON DELETE RESTRICT
);

CREATE TABLE events (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    tourist_spot_id UUID NOT NULL REFERENCES tourist_spots(tourist_spot_id) ON DELETE CASCADE
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tourist_spot_tags (
    tourist_spot_id UUID NOT NULL REFERENCES tourist_spots(tourist_spot_id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (tourist_spot_id, tag_id)
);

CREATE TABLE tourist_spot_tour_guides (
    tourist_spot_id UUID NOT NULL REFERENCES tourist_spots(tourist_spot_id) ON DELETE CASCADE,
    tour_guide_id UUID NOT NULL REFERENCES tour_guides(tour_guide_id) ON DELETE CASCADE,
    PRIMARY KEY (tourist_spot_id, tour_guide_id)
);

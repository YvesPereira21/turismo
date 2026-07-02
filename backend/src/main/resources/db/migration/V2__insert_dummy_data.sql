-- Insert States
INSERT INTO states (state_id, name) VALUES
('3a4f6d89-9a2c-473d-9865-c7cf1e95fa66', 'São Paulo'),
('7b5e8c12-3b4a-4e5c-bd8d-1234567890ab', 'Rio de Janeiro'),
('5c6d7e8f-9a0b-1c2d-3e4f-5a6b7c8d9e0f', 'Paraíba');

-- Insert Cities
INSERT INTO cities (city_id, name, state_id) VALUES
('2d3e4f5a-6b7c-8d9e-0f1a-2b3c4d5e6f7a', 'São Paulo', '3a4f6d89-9a2c-473d-9865-c7cf1e95fa66'),
('1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'Rio de Janeiro', '7b5e8c12-3b4a-4e5c-bd8d-1234567890ab'),
('8f9e0d1c-2b3a-4e5f-6a7b-8c9d0e1f2a3b', 'João Pessoa', '5c6d7e8f-9a0b-1c2d-3e4f-5a6b7c8d9e0f');

-- Insert Users
INSERT INTO users (id, name, email, password, phone, role) VALUES
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'João Manager', 'joao.manager@example.com', '$2a$10$xyz', '11999999999', 'SPOTMANAGER'),
('e83f2a1b-6c7d-4b5a-9f8e-7d6c5b4a3f2e', 'Maria Guide', 'maria.guide@example.com', '$2a$10$xyz', '21988888888', 'TOURGUIDE'),
('d1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a', 'Pedro Tourist', 'pedro.tourist@example.com', '$2a$10$xyz', '83977777777', 'TOURIST');

-- Insert Tourists
INSERT INTO tourists (tourist_id, birth_date, user_id) VALUES
('c3d4e5f6-a7b8-9c0d-1e2f-3a4b5c6d7e8f', '1995-05-15', 'd1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a');

-- Insert Spot Managers
INSERT INTO spot_managers (spot_manager_id, manager_type, user_id) VALUES
('b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 'PRIVATE', 'f47ac10b-58cc-4372-a567-0e02b2c3d479');

-- Insert Tour Guides
INSERT INTO tour_guides (tour_guide_id, cadastur, type, user_id) VALUES
('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '12.345678.90-1', 'REGIONAL', 'e83f2a1b-6c7d-4b5a-9f8e-7d6c5b4a3f2e');

-- Insert Tourist Spots
-- Coordinates (Longitude, Latitude) for PostGIS points (Geography matches lon/lat order):
-- MASP: -46.655881, -23.561414
-- Parque do Ibirapuera: -46.657639, -23.587416
-- Cristo Redentor: -43.210487, -22.951916
INSERT INTO tourist_spots (tourist_spot_id, name, location, opens_at, closes_at, short_description, description, spot_manager_id, city_id) VALUES
(
  '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 
  'MASP - Museu de Arte de São Paulo', 
  ST_GeographyFromText('SRID=4326;POINT(-46.655881 -23.561414)'), 
  '10:00:00', '18:00:00', 
  'Museu de arte icônico na Av. Paulista', 
  'O Museu de Arte de São Paulo Assis Chateaubriand é uma das mais importantes instituições culturais brasileiras.', 
  'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 
  '2d3e4f5a-6b7c-8d9e-0f1a-2b3c4d5e6f7a'
),
(
  '9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', 
  'Parque do Ibirapuera', 
  ST_GeographyFromText('SRID=4326;POINT(-46.657639 -23.587416)'), 
  '05:00:00', '23:00:00', 
  'O pulmão verde de São Paulo', 
  'O Parque Ibirapuera é o mais importante parque urbano da cidade de São Paulo, ideal para atividades físicas e lazer.', 
  'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 
  '2d3e4f5a-6b7c-8d9e-0f1a-2b3c4d5e6f7a'
),
(
  '1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 
  'Cristo Redentor', 
  ST_GeographyFromText('SRID=4326;POINT(-43.210487 -22.951916)'), 
  '08:00:00', '19:00:00', 
  'Uma das Sete Maravilhas do Mundo Moderno', 
  'O Cristo Redentor é uma estátua art déco que retrata Jesus Cristo, localizada no topo do morro do Corcovado.', 
  'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 
  '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d'
);

-- Insert Social Media
INSERT INTO socials_media (social_media_id, social_media_link, social_media_type, spot_manager_id) VALUES
('d2e3f4a5-b6c7-8d9e-0f1a-2b3c4d5e6f7a', 'https://instagram.com/joao_spots', 'INSTAGRAM', 'b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e');

-- Insert Photos
INSERT INTO photos (photo_id, url, tourist_spot_id) VALUES
('f1a2b3c4-d5e6-7a8b-9c0d-1e2f3a4b5c6d', 'https://exemplo.com/fotos/masp1.jpg', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23'),
('e2f3a4b5-c6d7-8e9f-0a1b-2c3d4e5f6a7b', 'https://exemplo.com/fotos/ibirapuera1.jpg', '9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d'),
('d3e4f5a6-b7c8-9d0e-1f2a-3b4c5d6e7f8a', 'https://exemplo.com/fotos/cristo1.jpg', '1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e');

-- Insert Activities
INSERT INTO activities (activity_id, name, tourist_spot_id, photo_id) VALUES
('b3c4d5e6-f7a8-9b0c-1d2e-3f4a5b6c7d8e', 'Apreciar obras de arte', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 'f1a2b3c4-d5e6-7a8b-9c0d-1e2f3a4b5c6d'),
('a2b3c4d5-e6f7-8a9b-0c1d-2e3f4a5b6c7d', 'Caminhar no parque', '9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', 'e2f3a4b5-c6d7-8e9f-0a1b-2c3d4e5f6a7b');

-- Insert Events
INSERT INTO events (id, name, description, event_date, tourist_spot_id) VALUES
('c4d5e6f7-a8b9-0c1d-2e3f-4a5b6c7d8e9f', 'Exposição Especial de Inverno', 'Uma exposição temporária de artistas contemporâneos brasileiros.', '2026-07-15', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23');

-- Insert Tags
INSERT INTO tags (id, name) VALUES
('e8f9a0b1-c2d3-4e5f-6a7b-8c9d0e1f2a3b', 'Cultura'),
('d7e8f9a0-b1c2-3d4e-5f6a-7b8c9d0e1f2a', 'Natureza'),
('c6d7e8f9-a0b1-2c3d-4e5f-6a7b8c9d0e1f', 'Aventura'),
('b5c6d7e8-f9a0-1b2c-3d4e-5f6a7b8c9d0e', 'Histórico');

-- Insert Tourist Spot Tags
INSERT INTO tourist_spot_tags (tourist_spot_id, tag_id) VALUES
('8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 'e8f9a0b1-c2d3-4e5f-6a7b-8c9d0e1f2a3b'),
('8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 'b5c6d7e8-f9a0-1b2c-3d4e-5f6a7b8c9d0e'),
('9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', 'd7e8f9a0-b1c2-3d4e-5f6a-7b8c9d0e1f2a');

-- Insert Tourist Spot Tour Guides
INSERT INTO tourist_spot_tour_guides (tourist_spot_id, tour_guide_id) VALUES
('1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d');

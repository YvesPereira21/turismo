-- Create Test Spot Manager User (password: 12345678)
INSERT INTO users (id, name, email, password, phone, role) VALUES
('a0a0a0a0-a0a0-4000-8000-a0a0a0a0a0a0', 'Gestor de Teste', 'gestor@teste.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '83999998888', 'SPOTMANAGER'),
('d0d0d0d0-d0d0-4000-8000-d0d0d0d0d0d0', 'Carlos Guia Silva', 'carlos.guia@example.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '83988887777', 'TOURGUIDE');

-- Also update existing João Manager's password to 12345678 so the user can test with joao.manager@example.com as well!
UPDATE users SET password = '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO' WHERE email = 'joao.manager@example.com';

-- Insert Spot Manager Entity
INSERT INTO spot_managers (spot_manager_id, manager_type, user_id) VALUES
('b0b0b0b0-b0b0-4000-8000-b0b0b0b0b0b0', 'PRIVATE', 'a0a0a0a0-a0a0-4000-8000-a0a0a0a0a0a0');

-- Insert Tour Guide Entity
INSERT INTO tour_guides (tour_guide_id, cadastur, type, user_id) VALUES
('e0e0e0e0-e0e0-4000-8000-e0e0e0e0e0e0', '98.765432.10-9', 'REGIONAL', 'd0d0d0d0-d0d0-4000-8000-d0d0d0d0d0d0');

-- Insert Tourist Spots for Gestor de Teste
INSERT INTO tourist_spots (tourist_spot_id, name, location, opens_at, closes_at, short_description, description, spot_manager_id, city_id) VALUES
(
  'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1', 
  'Parque Solon de Lucena (Lagoa)', 
  ST_GeographyFromText('SRID=4326;POINT(-34.8778 -7.1194)'), 
  '06:00:00', '22:00:00', 
  'Parque público no centro de João Pessoa', 
  'O Parque Solon de Lucena é um dos cartões postais mais belos de João Pessoa, cercado por palmeiras imperiais e uma grande lagoa central iluminada.', 
  'b0b0b0b0-b0b0-4000-8000-b0b0b0b0b0b0', 
  '8f9e0d1c-2b3a-4e5f-6a7b-8c9d0e1f2a3b'
),
(
  'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2', 
  'Farol do Cabo Branco', 
  ST_GeographyFromText('SRID=4326;POINT(-34.7931 -7.1475)'), 
  '07:00:00', '18:00:00', 
  'Ponto mais oriental das Américas', 
  'O Farol do Cabo Branco é um marco único localizado na Ponta do Seixas, simbolizando o ponto mais ao leste de todo o continente americano.', 
  'b0b0b0b0-b0b0-4000-8000-b0b0b0b0b0b0', 
  '8f9e0d1c-2b3a-4e5f-6a7b-8c9d0e1f2a3b'
);

-- Insert Photos
INSERT INTO photos (photo_id, url, tourist_spot_id) VALUES
('f0f0f0f0-f0f0-4000-8000-f0f0f0f0f0f1', 'https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?w=800', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1'),
('f0f0f0f0-f0f0-4000-8000-f0f0f0f0f0f2', 'https://images.unsplash.com/photo-1596422846543-75c6fc197f07?w=800', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2');

-- Insert Social Media for Tourist Spots
INSERT INTO socials_media (social_media_id, social_media_link, social_media_type, tourist_spot_id) VALUES
('11111111-2222-4000-8000-333333333331', 'https://instagram.com/lagoa_jp', 'INSTAGRAM', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1'),
('11111111-2222-4000-8000-333333333332', 'https://instagram.com/farol_cabobranco', 'INSTAGRAM', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2');

-- Link Tour Guides to Tourist Spots
INSERT INTO tourist_spot_tour_guides (tourist_spot_id, tour_guide_id) VALUES
('c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d'),
('c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1', 'e0e0e0e0-e0e0-4000-8000-e0e0e0e0e0e0'),
('c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2', 'e0e0e0e0-e0e0-4000-8000-e0e0e0e0e0e0');

INSERT INTO states (state_id, name) VALUES
('3a4f6d89-9a2c-473d-9865-c7cf1e95fa66', 'São Paulo'),
('7b5e8c12-3b4a-4e5c-bd8d-1234567890ab', 'Rio de Janeiro'),
('5c6d7e8f-9a0b-1c2d-3e4f-5a6b7c8d9e0f', 'Paraíba');

INSERT INTO cities (city_id, name, state_id) VALUES
('2d3e4f5a-6b7c-8d9e-0f1a-2b3c4d5e6f7a', 'São Paulo', '3a4f6d89-9a2c-473d-9865-c7cf1e95fa66'),
('1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'Rio de Janeiro', '7b5e8c12-3b4a-4e5c-bd8d-1234567890ab'),
('8f9e0d1c-2b3a-4e5f-6a7b-8c9d0e1f2a3b', 'João Pessoa', '5c6d7e8f-9a0b-1c2d-3e4f-5a6b7c8d9e0f');

INSERT INTO users (id, name, email, password, phone, role, provider) VALUES
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'João Manager', 'joao.manager@example.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '11999999999', 'SPOTMANAGER', 'LOCAL'),
('a0a0a0a0-a0a0-4000-8000-a0a0a0a0a0a0', 'Gestor de Teste', 'gestor@teste.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '83999998888', 'SPOTMANAGER', 'LOCAL'),
('e83f2a1b-6c7d-4b5a-9f8e-7d6c5b4a3f2e', 'Maria Guide', 'maria.guide@example.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '21988888888', 'TOURGUIDE', 'LOCAL'),
('d0d0d0d0-d0d0-4000-8000-d0d0d0d0d0d0', 'Carlos Guia Silva', 'carlos.guia@example.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '83988887777', 'TOURGUIDE', 'LOCAL'),
('d1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a', 'Pedro Tourist', 'pedro.tourist@example.com', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '83977777777', 'TOURIST', 'LOCAL'),
('b0a1b2c3-d4e5-4f6a-7b8c-9d0e1f2a3b4c', 'Administrador', 'admin@turismo.io', '$2a$10$TuP778C12ob2tfmtawXgA.0dl4FIt6SMcJUSpBQFzblxZqdDAraNO', '83999999999', 'ADMIN', 'LOCAL');

INSERT INTO tourists (tourist_id, birth_date, user_id) VALUES
('c3d4e5f6-a7b8-9c0d-1e2f-3a4b5c6d7e8f', '1995-05-15', 'd1e2f3a4-b5c6-7d8e-9f0a-1b2c3d4e5f6a');

INSERT INTO spot_managers (spot_manager_id, manager_type, user_id) VALUES
('b2c3d4e5-f6a7-8b9c-0d1e-2f3a4b5c6d7e', 'PRIVATE', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),
('b0b0b0b0-b0b0-4000-8000-b0b0b0b0b0b0', 'PRIVATE', 'a0a0a0a0-a0a0-4000-8000-a0a0a0a0a0a0');

INSERT INTO tour_guides (tour_guide_id, cadastur, type, user_id) VALUES
('a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d', '12.345678.90-1', 'REGIONAL', 'e83f2a1b-6c7d-4b5a-9f8e-7d6c5b4a3f2e'),
('e0e0e0e0-e0e0-4000-8000-e0e0e0e0e0e0', '98.765432.10-9', 'REGIONAL', 'd0d0d0d0-d0d0-4000-8000-d0d0d0d0d0d0');

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
),
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

INSERT INTO socials_media (social_media_id, social_media_link, social_media_type, tourist_spot_id) VALUES
('d2e3f4a5-b6c7-8d9e-0f1a-2b3c4d5e6f7a', 'https://instagram.com/masp_oficial', 'INSTAGRAM', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23'),
('11111111-2222-4000-8000-333333333331', 'https://instagram.com/lagoa_jp', 'INSTAGRAM', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1'),
('11111111-2222-4000-8000-333333333332', 'https://instagram.com/farol_cabobranco', 'INSTAGRAM', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2');

INSERT INTO photos (photo_id, url, alt_text, tourist_spot_id) VALUES
('f1a2b3c4-d5e6-7a8b-9c0d-1e2f3a4b5c6d', 'https://exemplo.com/fotos/masp1.jpg', 'Fachada do MASP na Avenida Paulista', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23'),
('e2f3a4b5-c6d7-8e9f-0a1b-2c3d4e5f6a7b', 'https://exemplo.com/fotos/ibirapuera1.jpg', 'Lago do Parque do Ibirapuera', '9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d'),
('d3e4f5a6-b7c8-9d0e-1f2a-3b4c5d6e7f8a', 'https://exemplo.com/fotos/cristo1.jpg', 'Estátua do Cristo Redentor', '1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e'),
('f0f0f0f0-f0f0-4000-8000-f0f0f0f0f0f1', 'https://images.unsplash.com/photo-1506012787146-f92b2d7d6d96?w=800', 'Lagoa Solon de Lucena iluminada', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1'),
('f0f0f0f0-f0f0-4000-8000-f0f0f0f0f0f2', 'https://images.unsplash.com/photo-1596422846543-75c6fc197f07?w=800', 'Farol do Cabo Branco ao amanhecer', 'c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2');

INSERT INTO activities (activity_id, name, tourist_spot_id, photo_id) VALUES
('b3c4d5e6-f7a8-9b0c-1d2e-3f4a5b6c7d8e', 'Apreciar obras de arte', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 'f1a2b3c4-d5e6-7a8b-9c0d-1e2f3a4b5c6d'),
('a2b3c4d5-e6f7-8a9b-0c1d-2e3f4a5b6c7d', 'Caminhar no parque', '9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', 'e2f3a4b5-c6d7-8e9f-0a1b-2c3d4e5f6a7b');

INSERT INTO events (id, name, description, event_date, tourist_spot_id) VALUES
('c4d5e6f7-a8b9-0c1d-2e3f-4a5b6c7d8e9f', 'Exposição Especial de Inverno', 'Uma exposição temporária de artistas contemporâneos brasileiros.', '2026-07-15', '8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23');

INSERT INTO tags (id, name) VALUES
('e8f9a0b1-c2d3-4e5f-6a7b-8c9d0e1f2a3b', 'Cultura'),
('d7e8f9a0-b1c2-3d4e-5f6a-7b8c9d0e1f2a', 'Natureza'),
('c6d7e8f9-a0b1-2c3d-4e5f-6a7b8c9d0e1f', 'Aventura'),
('b5c6d7e8-f9a0-1b2c-3d4e-5f6a7b8c9d0e', 'Histórico');

INSERT INTO tourist_spot_tags (tourist_spot_id, tag_id) VALUES
('8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 'e8f9a0b1-c2d3-4e5f-6a7b-8c9d0e1f2a3b'),
('8fa37d8e-6e2c-43ad-82df-e14f6b1cfb23', 'b5c6d7e8-f9a0-1b2c-3d4e-5f6a7b8c9d0e'),
('9f0b1c2d-3e4f-5a6b-7c8d-9e0f1a2b3c4d', 'd7e8f9a0-b1c2-3d4e-5f6a-7b8c9d0e1f2a');

INSERT INTO tourist_spot_tour_guides (tourist_spot_id, tour_guide_id) VALUES
('1b2c3d4e-5f6a-7b8c-9d0e-1f2a3b4c5d6e', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d'),
('c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1', 'a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d'),
('c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c1', 'e0e0e0e0-e0e0-4000-8000-e0e0e0e0e0e0'),
('c0c0c0c0-c0c0-4000-8000-c0c0c0c0c0c2', 'e0e0e0e0-e0e0-4000-8000-e0e0e0e0e0e0');

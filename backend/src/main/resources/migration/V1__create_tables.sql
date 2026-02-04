-- =========================
-- SEQUENCES
-- =========================
CREATE SEQUENCE album_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE artista_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE capa_album_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE regional_id_seq START WITH 1 INCREMENT BY 1;

-- =========================
-- TABLE: artista
-- =========================
CREATE TABLE artista (
    id BIGINT PRIMARY KEY DEFAULT nextval('artista_id_seq'),
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL
);

-- =========================
-- TABLE: album
-- =========================
CREATE TABLE album (
    id BIGINT PRIMARY KEY DEFAULT nextval('album_id_seq'),
    titulo VARCHAR(255) NOT NULL
);

-- =========================
-- TABLE: artista_album (N:N)
-- =========================
CREATE TABLE artista_album (
    artista_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artista_id, album_id),
    CONSTRAINT fk_artista_album_artista
        FOREIGN KEY (artista_id) REFERENCES artista(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artista_album_album
        FOREIGN KEY (album_id) REFERENCES album(id)
        ON DELETE CASCADE
);

-- =========================
-- TABLE: capa_album
-- =========================
CREATE TABLE capa_album (
    id BIGINT PRIMARY KEY DEFAULT nextval('capa_album_id_seq'),
    bucket VARCHAR(255) NOT NULL,
    hash VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    tamanho BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    CONSTRAINT fk_capa_album_album
        FOREIGN KEY (album_id) REFERENCES album(id)
        ON DELETE CASCADE
);

-- =========================
-- TABLE: regional
-- =========================
CREATE TABLE regional (
    id BIGINT PRIMARY KEY DEFAULT nextval('regional_id_seq'),
    id_externo INTEGER NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true
);

-- =========================
-- INDEXES (performance)
-- =========================
CREATE INDEX idx_artista_tipo ON artista(tipo);
CREATE INDEX idx_artista_album_artista_id ON artista_album(artista_id);
CREATE INDEX idx_artista_album_album_id ON artista_album(album_id);
CREATE INDEX idx_capa_album_album_id ON capa_album(album_id);
CREATE INDEX idx_regional_id_externo ON regional(id_externo);
CREATE INDEX idx_regional_ativo ON regional(ativo);

-- =========================
-- CARGA INICIAL (Exemplos do edital)
-- =========================

-- Artistas (SOLO e BANDA)
INSERT INTO artista (id, nome, tipo) VALUES
    (nextval('artista_id_seq'), 'Serj Tankian', 'SOLO'),
    (nextval('artista_id_seq'), 'Mike Shinoda', 'SOLO'),
    (nextval('artista_id_seq'), 'Michel Teló', 'SOLO'),
    (nextval('artista_id_seq'), 'Guns N'' Roses', 'BANDA');

-- Álbuns - Serj Tankian
INSERT INTO album (id, titulo) VALUES
    (nextval('album_id_seq'), 'Harakiri'),
    (nextval('album_id_seq'), 'Black Blooms'),
    (nextval('album_id_seq'), 'The Rough Dog');

-- Álbuns - Mike Shinoda
INSERT INTO album (id, titulo) VALUES
    (nextval('album_id_seq'), 'The Rising Tied'),
    (nextval('album_id_seq'), 'Post Traumatic'),
    (nextval('album_id_seq'), 'Post Traumatic EP'),
    (nextval('album_id_seq'), 'Where''d You Go');

-- Álbuns - Michel Teló
INSERT INTO album (id, titulo) VALUES
    (nextval('album_id_seq'), 'Bem Sertanejo'),
    (nextval('album_id_seq'), 'Bem Sertanejo - O Show (Ao Vivo)'),
    (nextval('album_id_seq'), 'Bem Sertanejo - (1ª Temporada) - EP');

-- Álbuns - Guns N' Roses
INSERT INTO album (id, titulo) VALUES
    (nextval('album_id_seq'), 'Use Your Illusion I'),
    (nextval('album_id_seq'), 'Use Your Illusion II'),
    (nextval('album_id_seq'), 'Greatest Hits');

-- Relacionamentos N:N
-- Serj Tankian
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Serj Tankian' AND al.titulo IN ('Harakiri', 'Black Blooms', 'The Rough Dog');

-- Mike Shinoda
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Mike Shinoda' AND al.titulo IN ('The Rising Tied', 'Post Traumatic', 'Post Traumatic EP', 'Where''d You Go');

-- Michel Teló
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Michel Teló' AND al.titulo IN ('Bem Sertanejo', 'Bem Sertanejo - O Show (Ao Vivo)', 'Bem Sertanejo - (1ª Temporada) - EP');

-- Guns N' Roses
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Guns N'' Roses' AND al.titulo IN ('Use Your Illusion I', 'Use Your Illusion II', 'Greatest Hits');

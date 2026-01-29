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
-- CARGA INICIAL (Exemplos N:N)
-- =========================

-- Artistas
INSERT INTO artista (id, nome, tipo) VALUES
    (nextval('artista_id_seq'), 'Queen', 'BANDA'),
    (nextval('artista_id_seq'), 'David Bowie', 'SOLO'),
    (nextval('artista_id_seq'), 'Freddie Mercury', 'SOLO');

-- Álbuns
INSERT INTO album (id, titulo) VALUES
    (nextval('album_id_seq'), 'A Kind of Magic'),
    (nextval('album_id_seq'), 'Under Pressure (Single)'),
    (nextval('album_id_seq'), 'Mr. Bad Guy');

-- Relacionamentos N:N (exemplos de colaboração)
-- "A Kind of Magic" - apenas Queen
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Queen' AND al.titulo = 'A Kind of Magic';

-- "Under Pressure" - Queen + David Bowie (colaboração clássica)
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Queen' AND al.titulo = 'Under Pressure (Single)';

INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'David Bowie' AND al.titulo = 'Under Pressure (Single)';

-- "Mr. Bad Guy" - Freddie Mercury solo
INSERT INTO artista_album (artista_id, album_id)
SELECT a.id, al.id FROM artista a, album al
WHERE a.nome = 'Freddie Mercury' AND al.titulo = 'Mr. Bad Guy';

-- =========================
-- SEQUENCES
-- =========================
CREATE SEQUENCE album_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE artista_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE capa_album_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE regional_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE genero_id_seq START WITH 1 INCREMENT BY 1;

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
    titulo VARCHAR(255) NOT NULL,
    ano INTEGER,
    data_cadastro TIMESTAMP DEFAULT NOW()
);

-- =========================
-- TABLE: genero
-- =========================
CREATE TABLE genero (
    id BIGINT PRIMARY KEY DEFAULT nextval('genero_id_seq'),
    nome VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    descricao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT true,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP,
    CONSTRAINT uk_genero_nome UNIQUE (nome),
    CONSTRAINT uk_genero_slug UNIQUE (slug)
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
-- TABLE: album_genero (N:N)
-- =========================
CREATE TABLE album_genero (
    album_id BIGINT NOT NULL,
    genero_id BIGINT NOT NULL,
    PRIMARY KEY (album_id, genero_id),
    CONSTRAINT fk_album_genero_album
        FOREIGN KEY (album_id) REFERENCES album(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_album_genero_genero
        FOREIGN KEY (genero_id) REFERENCES genero(id)
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
-- TABLE: usuario_artista_favorito
-- =========================
CREATE TABLE usuario_artista_favorito (
    usuario_id VARCHAR(255) NOT NULL,
    artista_id BIGINT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, artista_id),
    CONSTRAINT fk_fav_artista_artista
        FOREIGN KEY (artista_id) REFERENCES artista(id)
        ON DELETE CASCADE
);

-- =========================
-- TABLE: usuario_album_favorito
-- =========================
CREATE TABLE usuario_album_favorito (
    usuario_id VARCHAR(255) NOT NULL,
    album_id BIGINT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (usuario_id, album_id),
    CONSTRAINT fk_fav_album_album
        FOREIGN KEY (album_id) REFERENCES album(id)
        ON DELETE CASCADE
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
CREATE INDEX idx_genero_slug ON genero(slug);
CREATE INDEX idx_genero_ativo ON genero(ativo);
CREATE INDEX idx_album_genero_genero_id ON album_genero(genero_id);
CREATE INDEX idx_fav_artista_user ON usuario_artista_favorito(usuario_id);
CREATE INDEX idx_fav_album_user ON usuario_album_favorito(usuario_id);

-- =========================
-- SEED INICIAL (Idempotente)
-- =========================

-- 1. Gêneros
INSERT INTO genero (id, nome, slug, descricao) VALUES
    (nextval('genero_id_seq'), 'Rock', 'rock', 'Rock and Roll clássico e moderno'),
    (nextval('genero_id_seq'), 'Pop', 'pop', 'Música popular internacional'),
    (nextval('genero_id_seq'), 'Jazz', 'jazz', 'Jazz, Blues e Soul'),
    (nextval('genero_id_seq'), 'Hip Hop', 'hip-hop', 'Rap e cultura urbana'),
    (nextval('genero_id_seq'), 'Eletrônica', 'eletronica', 'House, Techno e Dance'),
    (nextval('genero_id_seq'), 'Clássico', 'classico', 'Música erudita e orquestral'),
    (nextval('genero_id_seq'), 'Sertanejo', 'sertanejo', 'Música sertaneja brasileira'),
    (nextval('genero_id_seq'), 'MPB', 'mpb', 'Música Popular Brasileira')
ON CONFLICT (slug) DO NOTHING;

-- 2. Artistas
INSERT INTO artista (id, nome, tipo) VALUES
    (nextval('artista_id_seq'), 'Queen', 'BANDA'),
    (nextval('artista_id_seq'), 'David Bowie', 'SOLO'),
    (nextval('artista_id_seq'), 'Freddie Mercury', 'SOLO'),
    (nextval('artista_id_seq'), 'Michael Jackson', 'SOLO'),
    (nextval('artista_id_seq'), 'Daft Punk', 'BANDA'),
    (nextval('artista_id_seq'), 'Elis Regina', 'SOLO'),
    (nextval('artista_id_seq'), 'Legião Urbana', 'BANDA'),
    (nextval('artista_id_seq'), 'Eminem', 'SOLO');

-- 3. Álbuns (10 álbuns variados)
INSERT INTO album (id, titulo, ano) VALUES
    (nextval('album_id_seq'), 'A Kind of Magic', 1986),
    (nextval('album_id_seq'), 'Under Pressure (Single)', 1981),
    (nextval('album_id_seq'), 'Mr. Bad Guy', 1985),
    (nextval('album_id_seq'), 'Thriller', 1982),
    (nextval('album_id_seq'), 'Bad', 1987),
    (nextval('album_id_seq'), 'Discovery', 2001),
    (nextval('album_id_seq'), 'Random Access Memories', 2013),
    (nextval('album_id_seq'), 'Falso Brilhante', 1976),
    (nextval('album_id_seq'), 'Dois', 1986),
    (nextval('album_id_seq'), 'The Eminem Show', 2002);

-- 4. Relacionamentos Artista-Álbum
-- Queen
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Queen' AND al.titulo = 'A Kind of Magic';
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Queen' AND al.titulo = 'Under Pressure (Single)';

-- David Bowie
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'David Bowie' AND al.titulo = 'Under Pressure (Single)';

-- Freddie Mercury
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Freddie Mercury' AND al.titulo = 'Mr. Bad Guy';

-- Michael Jackson
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Michael Jackson' AND al.titulo = 'Thriller';
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Michael Jackson' AND al.titulo = 'Bad';

-- Daft Punk
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Daft Punk' AND al.titulo = 'Discovery';
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Daft Punk' AND al.titulo = 'Random Access Memories';

-- Elis Regina
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Elis Regina' AND al.titulo = 'Falso Brilhante';

-- Legião Urbana
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Legião Urbana' AND al.titulo = 'Dois';

-- Eminem
INSERT INTO artista_album (artista_id, album_id) SELECT a.id, al.id FROM artista a, album al WHERE a.nome = 'Eminem' AND al.titulo = 'The Eminem Show';


-- 5. Relacionamentos Álbum-Gênero
-- Rock
INSERT INTO album_genero (album_id, genero_id) SELECT al.id, g.id FROM album al, genero g WHERE al.titulo IN ('A Kind of Magic', 'Under Pressure (Single)', 'Dois') AND g.slug = 'rock';

-- Pop
INSERT INTO album_genero (album_id, genero_id) SELECT al.id, g.id FROM album al, genero g WHERE al.titulo IN ('Mr. Bad Guy', 'Thriller', 'Bad') AND g.slug = 'pop';

-- Eletrônica
INSERT INTO album_genero (album_id, genero_id) SELECT al.id, g.id FROM album al, genero g WHERE al.titulo IN ('Discovery', 'Random Access Memories') AND g.slug = 'eletronica';

-- MPB
INSERT INTO album_genero (album_id, genero_id) SELECT al.id, g.id FROM album al, genero g WHERE al.titulo = 'Falso Brilhante' AND g.slug = 'mpb';

-- Hip Hop
INSERT INTO album_genero (album_id, genero_id) SELECT al.id, g.id FROM album al, genero g WHERE al.titulo = 'The Eminem Show' AND g.slug = 'hip-hop';

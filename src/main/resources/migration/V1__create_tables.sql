    -- =========================
    -- SEQUENCES
    -- =========================
    CREATE SEQUENCE album_id_seq START WITH 1 INCREMENT BY 1;
    CREATE SEQUENCE artista_id_seq START WITH 1 INCREMENT BY 1;
    CREATE SEQUENCE capa_album_id_seq START WITH 1 INCREMENT BY 1;

    -- =========================
    -- TABLE: album
    -- =========================
    CREATE TABLE album (
        id BIGINT PRIMARY KEY DEFAULT nextval('album_id_seq'),
        titulo VARCHAR(255) NOT NULL
    );

    -- =========================
    -- TABLE: artista
    -- =========================
    CREATE TABLE artista (
        id BIGINT PRIMARY KEY DEFAULT nextval('artista_id_seq'),
        nome VARCHAR(255) NOT NULL,
        tipo VARCHAR(50) NOT NULL
    );

    -- =========================
    -- TABLE: artista_album (Many-to-Many)
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
    -- INDEXES (performance)
    -- =========================
    CREATE INDEX idx_artista_tipo ON artista(tipo);
    CREATE INDEX idx_capa_album_album_id ON capa_album(album_id);

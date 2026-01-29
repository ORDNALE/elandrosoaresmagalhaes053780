-- =========================
-- SEQUENCE
-- =========================
CREATE SEQUENCE regional_id_seq START WITH 1 INCREMENT BY 1;

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
-- INDEX
-- =========================
CREATE INDEX idx_regional_id_externo ON regional(id_externo);
CREATE INDEX idx_regional_ativo ON regional(ativo);

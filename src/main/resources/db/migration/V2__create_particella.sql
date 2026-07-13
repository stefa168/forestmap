CREATE TABLE particella
(
    id          BIGSERIAL PRIMARY KEY        NOT NULL,
    comune      VARCHAR(16)                  NOT NULL,
    foglio      VARCHAR(16)                  NOT NULL,
    numero      VARCHAR(16)                  NOT NULL,
    geom        GEOMETRY(MultiPolygon, 4326) NOT NULL,
    ingested_at TIMESTAMPTZ                  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_particella_cfn UNIQUE (comune, foglio, numero)
);

CREATE INDEX idx_particella_geom ON particella USING GIST (geom);
CREATE INDEX idx_particella_comune_foglio ON particella (comune, foglio);
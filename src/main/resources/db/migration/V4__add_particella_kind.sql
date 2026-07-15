ALTER TABLE particella
    ADD kind varchar(16) DEFAULT 'ORDINARIA';

CREATE INDEX idx_particella_kind
    ON particella (kind);

ALTER TABLE particella
    ADD sezione char DEFAULT NULL;

ALTER TABLE particella
    DROP CONSTRAINT uq_particella_cfn;
ALTER TABLE particella
    ADD CONSTRAINT uq_particella_cfns
        UNIQUE NULLS NOT DISTINCT (comune, foglio, numero, sezione);

DROP INDEX idx_particella_comune_foglio;
CREATE INDEX idx_particella_comune_foglio_sezione
    ON particella (comune, foglio, sezione)
    NULLS NOT DISTINCT;
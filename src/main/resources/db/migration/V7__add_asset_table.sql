CREATE TABLE assets
(
    id                uuid                 DEFAULT uuidv7() PRIMARY KEY,

    -- storage location
    bucket            text        NOT NULL CHECK ( bucket IN ('QUARANTINE', 'MEDIA') ),

    -- declared at presign, overwritten with detected values at validation
    content_type      text        NOT NULL,
    size_bytes        bigint      NOT NULL CHECK ( size_bytes > 0 ),
    filename          text        NOT NULL CHECK ( LENGTH(filename) BETWEEN 1 AND 255),
    kind              text GENERATED ALWAYS AS ( SPLIT_PART(content_type, '/', 1) ) STORED,

    -- populated during validation
    sha256            text CHECK (sha256 ~ '^[0-9a-f]{64}$'),
    width             integer CHECK ( width > 0 ),
    height            integer CHECK ( height > 0 ),
    thumbhash         text,

    short_description text,
    description       text,
    state             text        NOT NULL
        CHECK ( state IN ('PENDING_UPLOAD',
                          'PENDING_VALIDATION',
                          'AVAILABLE',
                          'REJECTED',
                          'PENDING_DELETION') ),
    state_reason      text,
    owner_id          bigint,

    created_at        timestamptz NOT NULL DEFAULT NOW(),
    available_at      timestamptz,

    version           bigint      NOT NULL DEFAULT 0,

    -- an AVAILABLE asset must have been fully validated
    CONSTRAINT asset_available_is_validated CHECK (
        state <> 'AVAILABLE' OR (
            bucket = 'MEDIA' AND
            sha256 IS NOT NULL AND
            available_at IS NOT NULL
            )
        ),

    -- available images carry their intrinsic dimensions
    CONSTRAINT asset_available_image_has_dimensions CHECK (
        NOT (state = 'AVAILABLE' AND kind = 'image') OR (
            width IS NOT NULL AND
            height IS NOT NULL AND
            thumbhash IS NOT NULL
            )
        ),

    CONSTRAINT asset_rejected_has_reason CHECK (
        state <> 'REJECTED' OR state_reason IS NOT NULL
        )

);

-- sweep for abandoned presigns: tiny slice of the table
CREATE INDEX asset_pending_upload_idx ON assets (created_at)
    WHERE state = 'PENDING_UPLOAD';

-- sweep for objects awaiting reclamation
CREATE INDEX asset_pending_deletion_idx ON assets (created_at)
    WHERE state = 'PENDING_DELETION';

-- validation queue (only if you sweep rather than act on the confirm callback)
CREATE INDEX asset_pending_validation_idx ON assets (created_at)
    WHERE state = 'PENDING_VALIDATION';

CREATE INDEX asset_owner_idx ON assets (owner_id)
    WHERE state = 'AVAILABLE';

-- dedup lookups
CREATE INDEX asset_sha256_idx ON assets (sha256)
    WHERE sha256 IS NOT NULL;

COMMENT ON COLUMN assets.content_type IS
    'Client-declared at presign; replaced with magic-byte-detected value at validation.';
COMMENT ON COLUMN assets.size_bytes IS
    'Client-declared at presign; replaced with actual object size at validation.';
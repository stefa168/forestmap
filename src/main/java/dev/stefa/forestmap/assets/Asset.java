package dev.stefa.forestmap.assets;

import lombok.Builder;
import lombok.With;

import java.time.Instant;
import java.util.UUID;


@Builder
@With
public record Asset(
    UUID id, // UUIDv7
    String bucket, // Object Storage bucket currently holding the asset

    // ==== Details populated at presign ====
    String contentType,
    Long sizeBytes,
    String filename,

    // ==== Details populated on asset validation ====
    String sha256,
    Integer width, Integer height,
    String thumbhash,

    String shortDescription,
    String description,
    AssetState state,
    String stateReason,

    Long ownerId,         // for authorization checks
    Instant createdAt,    // By default assigned on DB insert
    Instant availableAt,
    Long version          // optimistic locking for safe edits
) {
  public enum AssetState {
    PENDING_UPLOAD,
    PENDING_VALIDATION,
    AVAILABLE,
    REJECTED,
    PENDING_DELETION,
  }
}

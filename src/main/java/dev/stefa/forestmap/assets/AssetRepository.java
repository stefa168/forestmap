package dev.stefa.forestmap.assets;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class AssetRepository {

  private static final String COLUMNS = """
      id, bucket, content_type, size_bytes, filename, sha256,
      width, height, thumbhash, description, state, state_reason,
      owner_id, created_at, available_at, version
      """;

  private final JdbcClient jdbc;

  /**
   * Called before presigning. Row must be committed before the URL is returned.
   */
  public void insertPending(Asset asset) {
    jdbc.sql("""
            INSERT INTO assets (id, bucket, content_type, size_bytes,
                               filename, description, state, owner_id)
            VALUES (:id, :bucket, :contentType, :sizeBytes,
                    :filename, :description, :state, :ownerId)
            """)
        .param("id", asset.id())
        .param("bucket", asset.bucket())
        .param("contentType", asset.contentType())
        .param("sizeBytes", asset.sizeBytes())
        .param("filename", asset.filename())
        .param("description", asset.description())
        .param("state", asset.state().name())
        .param("ownerId", asset.ownerId())
        .update();
  }

  public Optional<Asset> findById(UUID id) {
    return jdbc.sql("SELECT " + COLUMNS + " FROM assets WHERE id = :id")
        .param("id", id)
        .query(AssetRepository::mapAsset)
        .optional();
  }

  public List<Asset> findAvailableByOwner(Long ownerId) {
    return jdbc.sql("SELECT " + COLUMNS + """
             FROM assets
            WHERE owner_id = :ownerId AND state = 'AVAILABLE'
            ORDER BY available_at DESC
            """)
        .param("ownerId", ownerId)
        .query(AssetRepository::mapAsset)
        .list();
  }

  // ---- state transitions (compare-and-swap; false means someone got there first)

  /**
   * Client confirmed the PUT completed.
   */
  public boolean markPendingValidation(UUID id) {
    return jdbc.sql("""
            UPDATE assets
               SET state = 'PENDING_VALIDATION', version = version + 1
             WHERE id = :id AND state = 'PENDING_UPLOAD'
            """)
        .param("id", id)
        .update() == 1;
  }

  /**
   * Called after the object has been copied into the media bucket.
   */
  public boolean markAvailable(UUID id, Validation v) {
    return jdbc.sql("""
            UPDATE assets
               SET state        = 'AVAILABLE',
                   bucket       = 'MEDIA',
                   content_type = :contentType,
                   size_bytes   = :sizeBytes,
                   sha256       = :sha256,
                   width        = :width,
                   height       = :height,
                   thumbhash    = :thumbhash,
                   state_reason = NULL,
                   available_at = NOW(),
                   version      = version + 1
             WHERE id = :id AND state = 'PENDING_VALIDATION'
            """)
        .param("id", id)
        .param("contentType", v.contentType())
        .param("sizeBytes", v.sizeBytes())
        .param("sha256", v.sha256())
        .param("width", v.width())
        .param("height", v.height())
        .param("thumbhash", v.thumbhash())
        .update() == 1;
  }

  public boolean markRejected(UUID id, String reason) {
    return jdbc.sql("""
            UPDATE assets
               SET state = 'REJECTED', state_reason = :reason,
                   version = version + 1
             WHERE id = :id AND state IN ('PENDING_UPLOAD', 'PENDING_VALIDATION')
            """)
        .param("id", id)
        .param("reason", reason)
        .update() == 1;
  }

  public boolean markPendingDeletion(UUID id) {
    return jdbc.sql("""
            UPDATE assets
               SET state = 'PENDING_DELETION', version = version + 1
             WHERE id = :id AND state IN ('AVAILABLE', 'REJECTED')
            """)
        .param("id", id)
        .update() == 1;
  }

  // ---- sweeps

  public List<Asset> findAbandonedUploads(Instant cutoff, int limit) {
    return jdbc.sql("SELECT " + COLUMNS + """
             FROM assets
            WHERE state = 'PENDING_UPLOAD' AND created_at < :cutoff
            ORDER BY created_at
            LIMIT :limit
            """)
        .param("cutoff", cutoff)
        .param("limit", limit)
        .query(AssetRepository::mapAsset)
        .list();
  }

  public List<Asset> findPendingDeletion(int limit) {
    return jdbc.sql("SELECT " + COLUMNS + """
             FROM assets
            WHERE state = 'PENDING_DELETION'
            ORDER BY created_at
            LIMIT :limit
            """)
        .param("limit", limit)
        .query(AssetRepository::mapAsset)
        .list();
  }

  /**
   * Only after the object is confirmed gone from storage.
   */
  public boolean purge(UUID id) {
    return jdbc.sql("""
            DELETE FROM assets
             WHERE id = :id AND state = 'PENDING_DELETION'
            """)
        .param("id", id)
        .update() == 1;
  }

  // ---- user-facing metadata edit (optimistic locking)

  public boolean updateMetadata(UUID id, String filename,
                                String description, long expectedVersion) {
    return jdbc.sql("""
            UPDATE assets
               SET filename = :filename, description = :description,
                   version = version + 1
             WHERE id = :id AND version = :expectedVersion
                   AND state = 'AVAILABLE'
            """)
        .param("id", id)
        .param("filename", filename)
        .param("description", description)
        .param("expectedVersion", expectedVersion)
        .update() == 1;
  }

  // ---- mapping

  private static Asset mapAsset(ResultSet rs, int rowNum) throws SQLException {
    return Asset.builder()
        .id(rs.getObject("id", UUID.class))
        .bucket(rs.getString("bucket"))
        .contentType(rs.getString("content_type"))
        .sizeBytes(rs.getLong("size_bytes"))
        .filename(rs.getString("filename"))
        .sha256(rs.getString("sha256"))
        .width(rs.getObject("width", Integer.class))
        .height(rs.getObject("height", Integer.class))
        .thumbhash(rs.getString("thumbhash"))
        .description(rs.getString("description"))
        .state(Asset.AssetState.valueOf(rs.getString("state")))
        .stateReason(rs.getString("state_reason"))
        .ownerId(rs.getLong("owner_id"))
        .createdAt(toInstant(rs.getObject("created_at", OffsetDateTime.class)))
        .availableAt(toInstant(rs.getObject("available_at", OffsetDateTime.class)))
        .version(rs.getLong("version"))
        .build();
  }

  private static Instant toInstant(OffsetDateTime odt) {
    return odt == null ? null : odt.toInstant();
  }

  public record Validation(
      String contentType, long sizeBytes, String sha256,
      Integer width, Integer height, String thumbhash
  ) {}
}

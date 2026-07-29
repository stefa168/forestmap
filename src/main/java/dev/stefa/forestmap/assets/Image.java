package dev.stefa.forestmap.assets;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@Builder
@Table("images")
public class Image {
  @Id
  private Long id;

  @Column("content_type")
  private String contentType;   // e.g. "image/png"

  @Column("filename")
  private String filename;

  @Column("data")
  private byte[] data;          // maps to PostgreSQL bytea

  @Column("owner_id")
  private Long ownerId;         // for authorization checks

  @Column("uploaded_at")
  private Instant uploadedAt;

  @Version
  private Long version;         // optimistic locking for safe edits
}
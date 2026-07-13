package dev.stefa.forestmap;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Geometry;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(
    name = "particella",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_particella_cfn",
        columnNames = {"comune", "foglio", "numero"}
    )
)
public class Particella {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 16)
  private String comune;

  @Column(nullable = true, length = 16)
  private String sezione;

  @Column(nullable = false, length = 16)
  private String foglio;

  @Column(nullable = false, length = 32)
  private String numero;

  /// MultiPolygon in EPSG:4326. The columnDefinition makes Hibernate emit the
  /// proper PostGIS typmod when validating against the schema Flyway built.
  @Setter
  @Column(nullable = false, columnDefinition = "geometry(MultiPolygon,4326)")
  private Geometry geom;

  @Column(name = "ingested_at", nullable = false)
  private Instant ingestedAt = Instant.now();
}

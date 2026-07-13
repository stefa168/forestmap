package dev.stefa.forestmap;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticellaRepository extends JpaRepository<Particella, Long> {

  Optional<Particella> findByComuneAndFoglioAndNumero(String comune, String foglio, String numero);

  /// Idempotent insert. ST_Multi normalises Polygon -> MultiPolygon so the column
  /// type constraint never trips. Hibernate Spatial binds the JTS Geometry directly.
  ///
  /// Returns 1 if inserted, 0 if the (comune, foglio, numero) already exists.
/*  @Modifying
  @Transactional
  @Query(value = """
      INSERT INTO particella (comune, foglio, numero, geom, ingested_at)
      VALUES (:comune, :foglio, :numero, ST_Multi(:geom), NOW())
      ON CONFLICT (comune, foglio, numero) DO NOTHING
      """, nativeQuery = true)
  int upsert(
      @Param("comune") String comune,
      @Param("foglio") String foglio,
      @Param("numero") String numero,
      @Param("geom") Geometry geom
  );*/
  @Modifying
  @Transactional
  @Query(value = """
    INSERT INTO particella (comune, sezione, foglio, numero, geom, ingested_at)
    VALUES (:comune, :sezione, :foglio, :numero, ST_Multi(ST_GeomFromEWKB(:geom)), NOW())
    ON CONFLICT (comune, sezione, foglio, numero) DO NOTHING
    """, nativeQuery = true)
  int upsert(
      @Param("comune") String comune,
      @Param("sezione") String sezione,
      @Param("foglio") String foglio,
      @Param("numero") String numero,
      @Param("geom") byte[] geom   // EWKB bytes, SRID embedded
  );

  default int upsert(CadastralReference ref, byte[] geomEwkb) {
    return upsert(ref.comune(), ref.sezione(), ref.foglio(), ref.numero(), geomEwkb);
  }

  // Viewport fetch. bbox in lon/lat, EPSG:4326.
  @Query(value = """
      SELECT * FROM particella
      WHERE ST_Intersects(geom, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))
      """, nativeQuery = true)
  List<Particella> findWithinBbox(@Param("minLon") double minLon, @Param("minLat") double minLat,
                                  @Param("maxLon") double maxLon, @Param("maxLat") double maxLat);
}

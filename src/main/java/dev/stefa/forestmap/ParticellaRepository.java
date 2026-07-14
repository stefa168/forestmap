package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import org.geotools.geometry.jts.WKBReader;
import org.jspecify.annotations.NullMarked;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
@NullMarked
@AllArgsConstructor
public class ParticellaRepository {
  private final JdbcClient db;
  private static final WKBWriter WKB = new WKBWriter(2, true);

  @Transactional
  public int upsert(CadastralReference ref, Geometry geom) {
    byte[] geomEwkb = WKB.write(geom);

    return db.sql("""
            INSERT INTO particella (comune, sezione, foglio, numero, geom, ingested_at)
            VALUES (:comune, :sezione, :foglio, :numero, ST_Multi(ST_GeomFromEWKB(:geom)), NOW())
            ON CONFLICT (comune, sezione, foglio, numero) DO NOTHING
            """)
        .param("comune", ref.comune())
        .param("sezione", ref.sezione())
        .param("foglio", ref.foglio())
        .param("numero", ref.numero())
        .param("geom", geomEwkb)
        .update();
  }

  public List<Particella> findWithinBbox(double minLon, double minLat,
                                         double maxLon, double maxLat) {
    return db.sql("""
            SELECT id, comune, sezione, foglio, numero, st_asbinary(geom) AS geom, ingested_at
            FROM particella
            WHERE st_intersects(geom, st_makeenvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))
            """)
        .param("minLon", minLon).param("minLat", minLat)
        .param("maxLon", maxLon).param("maxLat", maxLat)
        .query(ParticellaRepository::mapRow)
        .list();
  }

  public static Particella mapRow(ResultSet rs, int rowNum) throws SQLException {
    Geometry geom;
    try {
      geom = new WKBReader().read(rs.getBytes("geom"));
      geom.setSRID(4326);
    } catch (ParseException e) {
      throw new SQLException("Bad geometry in row " + rs.getLong("id"), e);
    }

    return new Particella(
        rs.getLong("id"),
        rs.getString("comune"),
        rs.getString("sezione"),
        rs.getString("foglio"),
        rs.getString("numero"),
        null,
        geom,
        rs.getObject("ingested_at", Instant.class));
  }
}

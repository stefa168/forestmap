package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import org.geotools.geometry.jts.WKBReader;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
@NullMarked
@AllArgsConstructor
public class ParticellaRepository {
  private final JdbcClient db;
  private final DataSource dataSource;
  private static final WKBWriter WKB = new WKBWriter(2, true);

  @Transactional
  public int upsert(CadastralReference ref, Geometry geom) {
    byte[] geomEwkb = WKB.write(geom);

    return db.sql("""
            INSERT INTO particella (comune, sezione, foglio, numero, kind, geom, ingested_at)
            VALUES (:comune, :sezione, :foglio, :numero, :kind, ST_Multi(ST_GeomFromEWKB(:geom)), NOW())
            ON CONFLICT (comune, sezione, foglio, numero) DO NOTHING
            """)
        .param("comune", ref.comune())
        .param("sezione", ref.sezione())
        .param("foglio", ref.foglio())
        .param("numero", ref.numero())
        .param("kind", ref.kind().name())
        .param("geom", geomEwkb)
        .update();
  }

  @Transactional
  public int batchUpsert(List<ParsedParcel> parcels) {
    int n = parcels.size();
    var comuni = new String[n];
    var sezioni = new String[n];
    var fogli = new String[n];
    var numeri = new String[n];
    var kinds = new String[n];
    var geoms = new byte[n][];

    for (int i = 0; i < n; i++) {
      var p = parcels.get(i);
      var r = p.reference();
      comuni[i]  = r.comune();
      sezioni[i] = r.sezione();      // null is fine
      fogli[i]   = r.foglio();
      numeri[i]  = r.numero();
      kinds[i]  = r.kind().name();
      geoms[i] = WKB.write(p.geometry());
    }

    return db.sql("""
        INSERT INTO particella (comune, sezione, foglio, numero, kind, geom)
        SELECT c,s,f,n,k, ST_GeomFromEWKB(g)
        FROM UNNEST(:comuni, :sezioni, :fogli, :numeri, :kinds, :geoms) AS t(c,s,f,n,k,g)
        ON CONFLICT (comune, sezione, foglio, numero) DO NOTHING
        RETURNING id
        """)
        .param("comuni",  comuni)
        .param("sezioni", sezioni)
        .param("fogli",   fogli)
        .param("numeri",  numeri)
        .param("kinds",  kinds)
        .param("geoms",   geoms)
        .query(Long.class)
        .list()
        .size();
  }

  public List<Particella> findWithinBbox(double minLon, double minLat,
                                         double maxLon, double maxLat) {
    return db.sql("""
            SELECT id, comune, sezione, foglio, numero, kind, st_asbinary(geom) AS geom, ingested_at
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
        ParcelKind.fromString(rs.getString("kind")),
        geom,
        rs.getObject("ingested_at", Instant.class));
  }

  public List<@Nullable String> findParcelsInBboxAsGeoJSON(
      double minLon, double minLat,
      double maxLon, double maxLat,
      int limit
  ) {
    return db.sql("""
            SELECT JSON_BUILD_OBJECT(
               'type', 'Feature',
               'id', id,
               'geometry', ST_AsGeoJSON(geom)::json,
               'properties', JSON_BUILD_OBJECT(
                 'id', id,
                 'comune', comune,
                 'sezione', sezione,
                 'foglio', foglio,
                 'numero', numero,
                 'kind', kind
               )
            ) AS f
            FROM particella
            WHERE ST_Intersects(geom, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))
            LIMIT :limit + 1;
            """)
        .param("minLon", minLon).param("minLat", minLat)
        .param("maxLon", maxLon).param("maxLat", maxLat)
        .param("limit", limit)
        .query(String.class)
        .list();
  }
}

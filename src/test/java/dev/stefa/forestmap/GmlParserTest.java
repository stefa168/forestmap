package dev.stefa.forestmap;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// Parses a saved WFS response (no network). Requires:
///  - src/test/resources/fixtures/cadastral-parcels-sample.gml (a captured real response)
///
/// This is the regression test for the attribute-casing bug: before the fix it would
/// parse zero parcels and the first assertion below would fail.
class GmlParserTest {

  private static GmlParser parser;

  @BeforeAll
  static void setUp() throws Exception {
/*    String schemaDir = System.getProperty("cadastre.inspire.schema-dir",
        System.getenv("CADASTRE_INSPIRE_SCHEMA_DIR"));
    Assumptions.assumeTrue(schemaDir != null,
        "Set -Dcadastre.inspire.schema-dir to run the parser test");*/
//    Hints.putSystemDefault(Hints.ENTITY_RESOLVER, new InspireSchemaResolver(Path.of(schemaDir)));

    CoordinateReferenceSystem source = CRS.decode("urn:ogc:def:crs:EPSG::6706");
    CoordinateReferenceSystem target = CRS.decode("EPSG:4326", true);
    MathTransform transform = CRS.findMathTransform(source, target, true);
    parser = new GmlParser(transform);
  }

  @Test
  void parsesParcelsFromFixture() throws Exception {
    List<ParsedParcel> parcels;
    try (InputStream fixture = resource("/fixtures/cadastral-parcels-sample.xml")) {
      parcels = parser.parse(fixture);
    }

    // Would fail at zero before the casing fix.
    assertThat(parcels).isNotEmpty();

    ParsedParcel first = parcels.getFirst();
    assertThat(first.reference().comune()).isNotBlank();
    assertThat(first.reference().numero()).isNotBlank();

    Geometry geometry = first.geometry();
    assertThat(geometry).isNotNull();
    assertThat(geometry.getSRID()).isEqualTo(4326);

    // Axis-order guard: reprojected centroid must land inside Italy (lon/lat),
    // not transposed into the ocean.
    Point centroid = geometry.getCentroid();
    assertThat(centroid.getX()).as("longitude").isBetween(6.0, 19.0);
    assertThat(centroid.getY()).as("latitude").isBetween(35.0, 48.0);
  }

  private InputStream resource(String path) {
    InputStream in = getClass().getResourceAsStream(path);
    if (in == null) {
      throw new IllegalStateException("Missing test fixture: " + path);
    }
    return in;
  }
}

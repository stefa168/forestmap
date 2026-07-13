package dev.stefa.forestmap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
class IngestionTest {

  // A SMALL box over land you know has parcels — adjust to your own town.
  static final BoundingBox BOX = new BoundingBox(44.950359, 8.0129201, 44.956515, 8.0201801);

  @Autowired
  AdeWfsClient client;
  @Autowired
  GmlParser parser;
  @Autowired
  IngestionService ingestion;

  @Test
    // Rung 0: do all the beans wire up?
  void contextLoads() {
  }

  @Test
    // Rung 1: can we reach AdE and get GML back?
  void fetchesRawGml() throws Exception {
    try (InputStream in = client.getFeatures(BOX)) {
      String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      System.out.println(body);
    }
  }

  @Test
    // Rung 2: does it parse, and are coords sane?
  void parsesFeatures() throws Exception {
    List<ParsedParcel> parcels;
    try (InputStream in = client.getFeatures(BOX)) {
      parcels = parser.parse(in);
    }
    System.out.println("parsed " + parcels.size() + " parcels");
    if (!parcels.isEmpty()) {
      ParsedParcel p = parcels.getFirst();
      System.out.println("reference = " + p.reference());
      System.out.println("type      = " + p.geometry().getGeometryType());
      var c = p.geometry().getCentroid();
      System.out.println("centroid  = lon " + c.getX() + ", lat " + c.getY());
    }
  }

  @Test
  void diagnoseParsing() throws Exception {
    var config = new org.geotools.wfs.v2_0.WFSConfiguration();
    try (java.io.InputStream in = client.getFeatures(BOX)) {
      var pull = new org.geotools.xsd.PullParser(
          config, in, org.geotools.api.feature.simple.SimpleFeature.class);
      int n = 0;
      Object o;
      while ((o = pull.parse()) != null) {
        n++;
        if (n <= 2) {
          System.out.println("object " + n + " class = " + o.getClass().getName());
          if (o instanceof org.geotools.api.feature.simple.SimpleFeature f) {
            System.out.println("  type = " + f.getFeatureType().getTypeName());
            f.getFeatureType().getAttributeDescriptors().forEach(d ->
                System.out.println("    " + d.getLocalName() + " = " + f.getAttribute(d.getName())));
          }
        }
      }
      System.out.println("TOTAL parsed objects: " + n);
    }
  }
}

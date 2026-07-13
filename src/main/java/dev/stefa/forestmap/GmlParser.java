package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.wfs.v2_0.WFSConfiguration;
import org.geotools.xsd.PullParser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/// Parses an AdE WFS 2.0 response into [ParsedParcel]s.
///
/// Uses GeoTools' [PullParser], the same streaming reader the WFSDataStore
/// uses internally — but here we drive it ourselves, one feature at a time, which
/// keeps memory flat on large bbox responses and keeps the failure surface small.
/// The INSPIRE schema imports resolve through the system-default EntityResolver
/// configured in `InspireSchemaConfig`; without that, this is exactly where
/// parsing would fail.
///
/// Each INSPIRE `cp:CadastralParcel` arrives flattened into a SimpleFeature
/// whose attributes include `label`, `nationalCadastralReference`, and a
/// default geometry — the same shape observed in real responses from sibling INSPIRE
/// services.
@Slf4j
@AllArgsConstructor
@Component
public class GmlParser {

  private final MathTransform adeToWgs84;

  public List<ParsedParcel> parse(InputStream wfsResponse) throws Exception {
    PullParser parser = new PullParser(new WFSConfiguration(), wfsResponse, SimpleFeature.class);

    List<ParsedParcel> parsed = new ArrayList<>();
    Object next;
    while ((next = parser.parse()) != null) {
      if (!(next instanceof SimpleFeature feature)) {
        continue;
      }

      String nationalRef = asString(getIgnoreCase(feature, "nationalCadastralReference"));
      if (nationalRef == null) {
        continue; // not a parcel feature we care about
      }
//      log.info("Got national ref {}", nationalRef);

      String label = asString(getIgnoreCase(feature, "label"));
      // todo temporary fix
      CadastralReference reference;
      try {
        reference = CadastralReference.parse(nationalRef, label);
      } catch (IllegalArgumentException e) {
        log.error("Found an undexpected national reference number", e);
        continue;
      }

      // L'AdE utilizza https://mapserver.org/ogc/wfs_server.html. Come keyword per le feature utilizza questo attributo
      Geometry source = (Geometry) feature.getAttribute("msGeometry");
      if (source == null) {
        source = (Geometry) feature.getDefaultGeometry(); // fallback
      }
      if (source == null) {
        continue;
      }
      Geometry wgs84 = JTS.transform(source, adeToWgs84);
      wgs84.setSRID(4326);

      parsed.add(new ParsedParcel(reference, wgs84));
    }
    return parsed;
  }

  private static @Nullable Object getIgnoreCase(@NonNull SimpleFeature f, String name) {
    Object v = f.getAttribute(name);
    if (v != null) return v;
    for (var d : f.getFeatureType().getAttributeDescriptors()) {
      if (d.getLocalName().equalsIgnoreCase(name)) {
        return f.getAttribute(d.getName());
      }
    }
    return null;
  }

  private static String asString(Object value) {
    return value == null ? null : value.toString();
  }
}

package dev.stefa.forestmap;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// Geometry + referencing beans shared across the ingestion pipeline.
///
/// Axis order is the subtle bit. AdE declares its data as
/// `urn:ogc:def:crs:EPSG::6706`; in URN form GeoTools honours the
/// authoritative axis order, which for 6706 is latitude/longitude. PostGIS, by
/// contrast, stores SRID 4326 as longitude/latitude (X=lon, Y=lat). So we decode
/// the target with `longitudeFirst=true` and let the transform do the swap;
/// the JTS coordinates that come out are lon/lat, ready to persist.
///
/// If you already have a GeoToolsConfig, merge these definitions in — the axis
/// handling here is the part worth keeping.
@Configuration
public class GeoToolsConfig {

  /// SRID 4326, default precision. Lon/lat is implied by how we build geometries below.
  @Bean
  public GeometryFactory geometryFactory() {
    return new GeometryFactory(new PrecisionModel(), 4326);
  }

  /// Source CRS exactly as AdE declares it (lat/lon authoritative order).
  @Bean
  public CoordinateReferenceSystem sourceCrs() throws Exception {
    return CRS.decode("urn:ogc:def:crs:EPSG::6706");
  }

  /// Target CRS forced to lon/lat to match PostGIS storage convention.
  @Bean
  public CoordinateReferenceSystem targetCrs() throws Exception {
    return CRS.decode("EPSG:4326", true);
  }

  /// 6706 (lat/lon) -> 4326 (lon/lat). Lenient: tolerates the RDN2008/WGS84 datum nuance.
  @Bean
  public MathTransform adeToWgs84(CoordinateReferenceSystem sourceCrs, CoordinateReferenceSystem targetCrs) throws Exception {
    return CRS.findMathTransform(sourceCrs, targetCrs, true);
  }
}

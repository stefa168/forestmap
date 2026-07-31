package dev.stefa.forestmap;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.PolygonArea;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/// A bounding box in EPSG:6706 (latitude/longitude order — the order AdE's BBOX parameter expects).
/// Stored as separate lat/lon components precisely so the axis order can't be confused at the call site.
@NullMarked
public record BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {

  /**
   * Four quadrants, for quadtree subdivision when the server truncates a response.
   */
  public List<BoundingBox> quarters() {
    double midLat = (minLat + maxLat) / 2.0;
    double midLon = (minLon + maxLon) / 2.0;
    return List.of(
      new BoundingBox(minLat, minLon, midLat, midLon),
      new BoundingBox(minLat, midLon, midLat, maxLon),
      new BoundingBox(midLat, minLon, maxLat, midLon),
      new BoundingBox(midLat, midLon, maxLat, maxLon));
  }

  /**
   * NOTE: orthodromicDistance takes (x, y) = (lon, lat) — inverted vs. this record's field order.
   */
  public double widthMeters() {
    double midLat = (minLat + maxLat) / 2.0;
    return DefaultEllipsoid.WGS84.orthodromicDistance(minLon, midLat, maxLon, midLat);
  }

  public double heightMeters() {
    return DefaultEllipsoid.WGS84.orthodromicDistance(minLon, minLat, minLon, maxLat);
  }

  /**
   * Longest side in metres — the natural unit for a subdivision floor.
   */
  public double spanMeters() {
    return Math.max(widthMeters(), heightMeters());
  }

  public double areaSquareKm() {
    PolygonArea p = new PolygonArea(Geodesic.WGS84, false);
    p.AddPoint(minLat, minLon);
    p.AddPoint(minLat, maxLon);
    p.AddPoint(maxLat, maxLon);
    p.AddPoint(maxLat, minLon);
    return Math.abs(p.Compute(false, true).area) / 1e6;
    // return widthMeters() * heightMeters() / 1_000_000.0;
  }

  @Override
  public String toString() {
    return "[%.6f,%.6f -> %.6f,%.6f]".formatted(minLat, minLon, maxLat, maxLon);
  }
}

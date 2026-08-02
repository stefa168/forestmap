package dev.stefa.forestmap;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;

@Slf4j
public class ClosingGeometryFactory extends GeometryFactory {
  private static final double TOLERANCE_METERS = 0.05;
  private static final double METERS_PER_DEGREE = 111_320.0;

  @Override
  public LinearRing createLinearRing(CoordinateSequence seq) {
    if (seq != null && seq.size() >= 4) {
      Coordinate first = seq.getCoordinate(0);
      Coordinate last = seq.getCoordinate(seq.size() - 1);

      if (!first.equals2D(last)) {
        double gap = gapMeters(first, last);
        if (gap > TOLERANCE_METERS) {
          log.error("Got a ring with coordinates that don't close exactly ({} meters, MORE than tolerance of {})", gap, TOLERANCE_METERS);
          throw new IllegalArgumentException(
              "Ring endpoints differ by %.9f deg, too far to be rounding: %s vs %s"
                  .formatted(gap, first, last));
        }

        log.warn("Got a ring with coordinates that don't close exactly ({} meters). Fixing it manually.", gap);
        Coordinate[] pts = seq.toCoordinateArray();
        pts[pts.length - 1] = pts[0].copy();
        return super.createLinearRing(getCoordinateSequenceFactory().create(pts));
      }
    }

    return super.createLinearRing(seq);
  }

  private static double gapMeters(@NonNull Coordinate a, @NonNull Coordinate b) {
    // EPSG:6706 as parsed from AdE: x = latitude, y = longitude
    double northing = (a.x - b.x) * METERS_PER_DEGREE;
    double easting = (a.y - b.y) * METERS_PER_DEGREE * Math.cos(Math.toRadians((a.x + b.x) / 2.0));
    return Math.hypot(northing, easting);
  }

}

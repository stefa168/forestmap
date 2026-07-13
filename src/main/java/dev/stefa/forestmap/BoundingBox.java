package dev.stefa.forestmap;

import java.util.List;

/**
 * A bounding box in EPSG:6706 (latitude/longitude order — the order AdE's BBOX
 * parameter expects). Stored as separate lat/lon components precisely so the
 * axis order can't be confused at the call site.
 */
public record BoundingBox(double minLat, double minLon, double maxLat, double maxLon) {

    /** Four quadrants, for quadtree subdivision when the server truncates a response. */
    public List<BoundingBox> quarters() {
        double midLat = (minLat + maxLat) / 2.0;
        double midLon = (minLon + maxLon) / 2.0;
        return List.of(
                new BoundingBox(minLat, minLon, midLat, midLon),
                new BoundingBox(minLat, midLon, midLat, maxLon),
                new BoundingBox(midLat, minLon, maxLat, midLon),
                new BoundingBox(midLat, midLon, maxLat, maxLon));
    }

    @Override
    public String toString() {
        return "[%.6f,%.6f -> %.6f,%.6f]".formatted(minLat, minLon, maxLat, maxLon);
    }
}

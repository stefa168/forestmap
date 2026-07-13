package dev.stefa.forestmap;

import org.locationtech.jts.geom.Geometry;

/** A parcel parsed from a WFS response: cadastral key + geometry already reprojected to 4326 (lon/lat). */
public record ParsedParcel(CadastralReference reference, Geometry geometry) {
}

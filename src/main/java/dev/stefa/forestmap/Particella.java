package dev.stefa.forestmap;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.locationtech.jts.geom.Geometry;

import java.time.Instant;

@NullMarked
public record Particella(
    Long id,
    String comune, String sezione, String foglio, String numero,
    @Nullable ParcelKind kind,
    Geometry geom, //geometry(MultiPolygon,4326)
    Instant ingestedAt
) {}
package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/// Drives a bounding-box sweep. Because AdE silently caps results, a response that
/// comes back full is treated as truncated: the box is split into quadrants and
/// each is ingested recursively until every leaf returns under the cap. Upserts are
/// idempotent on (comune, foglio, numero), so re-running any box is safe.
@Slf4j
@AllArgsConstructor
@Service
public class IngestionService {

  private final AdeWfsClient client;
  private final GmlParser parser;
  private final ParticellaRepository repository;

  /**
   * Ingest one bounding box, subdividing on overflow. Returns the number of parcels seen.
   */
  public int ingest(BoundingBox box) {
    List<ParsedParcel> parcels;
    try (InputStream response = client.getFeatures(box)) {
      parcels = parser.parse(response);
    } catch (Exception e) {
      throw new IngestionException("Failed to ingest bbox " + box, e);
    }

    if (parcels.size() >= client.maxFeatures() - 1) {
      log.info("bbox {} hit the {}-feature cap; subdividing", box, client.maxFeatures());
      int total = 0;
      for (BoundingBox quarter : box.quarters()) {
        total += ingest(quarter);
      }
      // todo log here the total amount of parcels for the request! (or in another place but here looks appropriate)
      return total;
    }

    parcels.forEach(p -> repository.upsert(p.reference(), p.geometry()));

    log.info("bbox {} -> {} parcels upserted", box, parcels.size());
    return parcels.size();
  }

  /// Thrown when a bbox cannot be fetched or parsed.
  public static class IngestionException extends RuntimeException {
    public IngestionException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/// Drives a bounding-box sweep. Because AdE silently caps results, a response that comes back full is treated as
/// truncated: the box is split into quadrants and each is ingested recursively until every leaf returns under the cap.
/// Upserts are idempotent on (comune, foglio, numero), so re-running any box is safe.
@Slf4j
@AllArgsConstructor
@Service
public class IngestionService {
  private static final double WORK_CELL_KM2 = 1.0;   // pre-split target; tune from observed cap
  private static final double TRUST_EMPTY_KM2 = 0.3;   // below this, empty means empty
  private static final int MAX_REQUESTS = 300;   // hard budget per run
  private static final double MAX_ROOT_KM2 = 15.0;  // reject bigger asks outright

  private final AdeWfsClient client;
  private final GmlParser parser;
  private final ParticellaRepository repository;

  /// Ingest one bounding box, subdividing on overflow. Returns the number of parcels seen.
  public int ingest(BoundingBox box) throws XMLStreamException {
    log.info("New ingestion request {} for a total of {}Km² ({}m)", box, "%.2f".formatted(box.areaSquareKm()), "%.2f".formatted(box.spanMeters()));
    var page = client.getFeatures(box);

//    if(page.isEmpty() || page.isTruncated(client.maxFeatures()))
    if (page.isEmpty()) {
      log.info("AdE returned 0 features.");
      return 0;
    }

    List<ParsedParcel> parcels;
    try (InputStream in = page.body()) {
      parcels = parser.parse(in);
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

    int added = parcels.stream()
        .map(p -> repository.upsert(p.reference(), p.geometry()))
        .reduce(0, Integer::sum);

    log.info("bbox {} -> {} parcels found, {} added", box, parcels.size(), added);
    return added;
  }

  public IngestionReport ingest2(BoundingBox root) throws XMLStreamException {
    log.info("New ingestion request {} for a total of {}Km² ({}m)", root, "%.2f".formatted(root.areaSquareKm()), "%.2f".formatted(root.spanMeters()));
    if (root.areaSquareKm() > MAX_ROOT_KM2)
      throw new IngestionException("Requested %.1f km² exceeds the %.0f km² per-run limit".formatted(root.areaSquareKm(), MAX_ROOT_KM2));

    Deque<BoundingBox> queue = seed(root);

    int requests = 0, found = 0, added = 0;

    while (!queue.isEmpty() && requests < MAX_REQUESTS) {
      BoundingBox box = queue.poll();
      requests++;

      WfsPage page = client.getFeatures(box);

      if (page.isTruncated(client.maxFeatures()) ||
          (page.isEmpty() && box.areaSquareKm() > TRUST_EMPTY_KM2)) {
        queue.addAll(box.quarters()); // no parse; header told us enough
        continue;
      }
      if (page.isEmpty()) continue; // small and empty: believed

      List<ParsedParcel> parcels;
      try (InputStream in = page.body()) {
        parcels = parser.parse(in);
      } catch (Exception e) {
        throw new IngestionException("Failed to ingest bbox " + box, e);
      }

      found += parcels.size();
      added += repository.batchUpsert(parcels);
      log.info(
          "bbox {} -> {} parcels found, {} added ({}/{} requests)",
          box, parcels.size(), added, requests, MAX_REQUESTS
      );
    }

    return new IngestionReport(found, added, requests, List.copyOf(queue));
  }

  /// Halve until each cell is at or below the work size; no network involved.
  private Deque<BoundingBox> seed(BoundingBox root) {
    Deque<BoundingBox> queue = new ArrayDeque<>();
    Deque<BoundingBox> pending = new ArrayDeque<>(List.of(root));

    while (!pending.isEmpty()) {
      var b = pending.poll();
      if (b.areaSquareKm() <= WORK_CELL_KM2) queue.add(b);
      else pending.addAll(b.quarters());
    }

    return queue;
  }

  public record IngestionReport(int found, int added, int requests, List<BoundingBox> unfinished) {}

  /// Thrown when a bbox cannot be fetched or parsed.
  public static class IngestionException extends RuntimeException {
    public IngestionException(String message) {super(message);}

    public IngestionException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}

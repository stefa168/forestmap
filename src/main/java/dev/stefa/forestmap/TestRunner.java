package dev.stefa.forestmap;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.XMLStreamException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class TestRunner {
  private IngestionService ingestionService;
  private ParticellaRepository repository;

  @PostMapping("/ingest")
  public int ingestBBox(@RequestBody BoundingBox box) throws XMLStreamException {
    return ingestionService.ingest(box);
  }

  public record FeatureCollection(String type, int count, boolean truncated, @JsonRawValue List<String> features) {}

  @GetMapping(value = "/parcels", produces = MediaType.APPLICATION_JSON_VALUE)
  public FeatureCollection parcels(@RequestParam double minLon, @RequestParam double minLat,
                                   @RequestParam double maxLon, @RequestParam double maxLat) {
    final int limit = 5000;
    var features = repository.findParcelsInBboxAsGeoJSON(minLon, minLat, maxLon, maxLat, limit);

    boolean truncated = features.size() >= limit;

    return new FeatureCollection("FeatureCollection", features.size(), truncated, features);
  }
}

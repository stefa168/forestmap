package dev.stefa.forestmap;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/admin")
public class TestRunner {
  private IngestionService ingestionService;

  @GetMapping("/ingest")
  public int ingestBBox(@RequestBody BoundingBox box) {
    return ingestionService.ingest(box);
  }
}

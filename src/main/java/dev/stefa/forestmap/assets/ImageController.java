package dev.stefa.forestmap.assets;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

@AllArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageController {
  ImageRepository repository;

  @GetMapping("/{id}")
  public ResponseEntity<byte[]> get(@PathVariable long id) {
    return repository.findById(id)
        .map(image -> ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.getContentType()))
            .body(image.getData())
        )
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Long> upload(@RequestParam MultipartFile file,
                                     @RequestParam Long ownerId) throws IOException {
    var b = Image.builder()
        .contentType(file.getContentType())
        .filename(file.getOriginalFilename())
        .data(file.getBytes())
        .ownerId(ownerId)
        .uploadedAt(Instant.now());

    return ResponseEntity.ok(repository.save(b.build()).getId());
  }
}

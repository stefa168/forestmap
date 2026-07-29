package dev.stefa.forestmap.association;

import dev.stefa.forestmap.association.Dto.AssociationName;
import dev.stefa.forestmap.association.Dto.PatchAssociationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/associations")
public class AssociationController {
  AssociationService service;

  @GetMapping
  public List<Association> getAll() {
    return service.getAll();
  }

  @GetMapping("/names")
  public List<AssociationName> getNames() {
    return service.getAssociationsNames();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Association> findById(@PathVariable long id) {
    var opt = service.findById(id);
    return opt
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public Long create(@NotEmpty @Length(min = 3) String associationName) {
    return service.createAssociation(associationName).getId();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> patch(@PathVariable Long id,
                                    @Valid @RequestBody PatchAssociationRequest request) {
    return service.patch(id, request)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}

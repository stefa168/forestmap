package dev.stefa.forestmap.association;

import dev.stefa.forestmap.association.Dto.AssociationName;
import dev.stefa.forestmap.association.Dto.PatchAssociationRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AssociationService {
  private final AssociationRepository associationRepository;
//  private final ImageRepository imageRepository;

  public boolean patch(Long id, PatchAssociationRequest request) {
    return associationRepository.findById(id)
        .map(association -> {
          if (request.name() != null)
            association.setName(request.name());

          if (request.logoId() != null) {
            // todo usare l'assetService
/*            if (!imageRepository.existsById(request.logoId())) {
              throw new IllegalArgumentException("Referenced image does not exist");
            }*/
            association.setLogoId(request.logoId());
          }

          associationRepository.save(association);
          return true;
        })
        .orElse(false);
  }

  public List<Association> getAll() {
    return IterableUtils.toList(associationRepository.findAll());
  }

  public List<AssociationName> getAssociationsNames() {
    return associationRepository.findAssociationNames();
  }

  public Association createAssociation(String associationName) {
    return associationRepository.save(
        Association.builder()
            .name(associationName)
            .build()
    );
  }

  public Optional<Association> findById(long id) {
    return associationRepository.findById(id);
  }
}

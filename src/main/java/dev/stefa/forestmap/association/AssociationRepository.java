package dev.stefa.forestmap.association;

import dev.stefa.forestmap.association.Dto.AssociationName;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AssociationRepository extends CrudRepository<Association, Long> {
  @Query("""
      SELECT id, name
      FROM associations
      """)
  List<AssociationName> findAssociationNames();
}

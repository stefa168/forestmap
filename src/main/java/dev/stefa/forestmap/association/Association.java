package dev.stefa.forestmap.association;

import dev.stefa.forestmap.assets.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Getter
@Setter
@Table("associations")
public class Association {
  @Id
  Long id;

  @Column("name")
  String name;

  @Reference(Image.class)
  @Column("logo")
  Long logoId;

  @Version
  Long version;
}

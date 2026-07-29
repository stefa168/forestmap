package dev.stefa.forestmap.association;

public final class Dto {
  public record PatchAssociationRequest(
      String name,
      Long logoId
  ) {}

  public record AssociationName(
      Long id,
      String name
  ) {}
}
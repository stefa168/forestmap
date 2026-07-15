package dev.stefa.forestmap;

import org.jspecify.annotations.NullMarked;

@NullMarked
public enum ParcelKind {
  ORDINARIA, STRADA, ACQUA, ALTRO;

  public static ParcelKind fromNumero(String numero) {
    String n = numero.toLowerCase();
    for (ParcelKind k : ParcelKind.values())
      if (n.contains(k.name().toLowerCase())) return k;
    return ORDINARIA;
  }

  public static ParcelKind fromString(String in) {
    for (ParcelKind s : values())
      if (s.name().equalsIgnoreCase(in)) return s;
    throw new IllegalArgumentException("Unknown parcel kind: " + in);
  }
}

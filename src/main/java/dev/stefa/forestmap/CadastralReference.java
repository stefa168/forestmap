package dev.stefa.forestmap;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/// The Italian cadastral key, derived from INSPIRE attributes.
///
/// From a published AdE example, `nationalCadastralReference = "G273_003400.1298"`
/// and `label = "1298"` decompose as:
///
/// <pre>
///   G273    -&gt; comune (codice catastale Belfiore: letter + 3 digits)
///   003400  -&gt; foglio block (foglio + sezione/allegato)  &lt;-- VERIFY this layout
///   1298    -&gt; particella (== label)
/// </pre>
///
/// **Verify the foglio block against a parcel you know** before trusting it
/// in production. The comune segment (before `_`) and the particella (== label, or after
/// the final `.`) are unambiguous; the internal split of the middle block is the one
/// assumption that the published example alone cannot confirm.
@NullMarked
public record CadastralReference(String comune, @Nullable String sezione, String foglio, String numero, ParcelKind kind) {

  public static CadastralReference parse(String nationalRef, @Nullable String label) {
    if (nationalRef.length() < 12 || nationalRef.charAt(11) != '.') {
      // 4+1+4+1+1 = 11 chars before the dot
      throw new IllegalArgumentException("Unexpected reference format: " + nationalRef);
    }
    String comune = nationalRef.substring(0, 4);
    char sezChar = nationalRef.charAt(4);
    String sezione = (sezChar == '_') ? null : String.valueOf(sezChar);
    String foglio = stripLeadingZeros(nationalRef.substring(5, 9));
    // charAt(9)=allegato, charAt(10)=sviluppo — capture later if needed
    String numero = (label != null && !label.isBlank())
        ? label.trim()
        : nationalRef.substring(12);
    return new CadastralReference(comune, sezione, foglio, numero, ParcelKind.fromNumero(numero));
  }

  private static String stripLeadingZeros(String s) {
    String trimmed = s.replaceFirst("^0+", "");
    return trimmed.isEmpty() ? "0" : trimmed;
  }
}

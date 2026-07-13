package dev.stefa.forestmap;

/**
 * The Italian cadastral key, derived from INSPIRE attributes.
 *
 * <p>From a published AdE example, {@code nationalCadastralReference = "G273_003400.1298"}
 * and {@code label = "1298"} decompose as:
 * <pre>
 *   G273    -> comune (codice catastale Belfiore: letter + 3 digits)
 *   003400  -> foglio block (foglio + sezione/allegato)  <-- VERIFY this layout
 *   1298    -> particella (== label)
 * </pre>
 *
 * <p><strong>Verify the foglio block against a parcel you know</strong> before trusting it
 * in production. The comune segment (before {@code _}) and the particella (== label, or after
 * the final {@code .}) are unambiguous; the internal split of the middle block is the one
 * assumption that the published example alone cannot confirm.
 */
public record CadastralReference(String comune, String sezione, String foglio, String numero) {

  public static CadastralReference parse(String nationalRef, String label) {
        /*if (nationalRef == null || nationalRef.isBlank()) {
            throw new IllegalArgumentException("Missing nationalCadastralReference");
        }
        int underscore = nationalRef.indexOf('_');
        int lastDot = nationalRef.lastIndexOf('.');
        if (underscore < 0 || lastDot < 0 || lastDot <= underscore) {
            throw new IllegalArgumentException("Unexpected reference format: " + nationalRef);
        }

        String comune = nationalRef.substring(0, underscore);
        String foglioBlock = nationalRef.substring(underscore + 1, lastDot);

        // Assumption under review: leading 4 chars = foglio, remainder = sezione/allegato.
        String foglioRaw = foglioBlock.length() >= 4 ? foglioBlock.substring(0, 4) : foglioBlock;
        String foglio = stripLeadingZeros(foglioRaw);

        String numero = (label != null && !label.isBlank())
                ? label.trim()
                : nationalRef.substring(lastDot + 1);

        return new CadastralReference(comune, foglio, numero);*/
    if (nationalRef == null || nationalRef.length() < 12 || nationalRef.charAt(11) != '.') {
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
    return new CadastralReference(comune, sezione, foglio, numero);
  }

  private static String stripLeadingZeros(String s) {
    String trimmed = s.replaceFirst("^0+", "");
    return trimmed.isEmpty() ? "0" : trimmed;
  }
}

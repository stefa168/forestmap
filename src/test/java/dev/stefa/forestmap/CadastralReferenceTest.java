package dev.stefa.forestmap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CadastralReferenceTest {

  @ParameterizedTest(name = "{0} -> {2}/{3}/{4}")
  @CsvSource({
      // nationalRef,      label, comune, foglio, numero
      "H366_000200.120,    120,   H366,   2,      120",
      "H366_000200.123,    123,   H366,   2,      123",
      "G273_003400.1298,   1298,  G273,   34,     1298",
  })
  void parsesRealReferences(String ref, String label, String comune, String foglio, String numero) {
    CadastralReference parsed = CadastralReference.parse(ref, label);
    assertThat(parsed.comune()).isEqualTo(comune);
    assertThat(parsed.foglio()).isEqualTo(foglio);
    assertThat(parsed.numero()).isEqualTo(numero);
  }

  @Test
  void fallsBackToReferenceTailWhenLabelMissing() {
    // numero should still resolve from the part after the final dot
    assertThat(CadastralReference.parse("H366_000200.120", null).numero()).isEqualTo("120");
    assertThat(CadastralReference.parse("H366_000200.120", "  ").numero()).isEqualTo("120");
  }

  @Test
  void rejectsMalformedReference() {
    assertThatThrownBy(() -> CadastralReference.parse("not-a-reference", "1"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsBlankReference() {
    assertThatThrownBy(() -> CadastralReference.parse("   ", "1"))
        .isInstanceOf(IllegalArgumentException.class);
  }

}

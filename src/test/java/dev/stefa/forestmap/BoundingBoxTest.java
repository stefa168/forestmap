package dev.stefa.forestmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class BoundingBoxTest {

  private static final BoundingBox PARENT = new BoundingBox(44.0, 8.0, 46.0, 10.0);

  @Test
  void splitsIntoFourQuarters() {
    assertThat(PARENT.quarters()).hasSize(4);
  }

  @Test
  void quartersStayWithinParent() {
    assertThat(PARENT.quarters()).allSatisfy(b -> {
      assertThat(b.minLat()).isBetween(44.0, 46.0);
      assertThat(b.maxLat()).isBetween(44.0, 46.0);
      assertThat(b.minLon()).isBetween(8.0, 10.0);
      assertThat(b.maxLon()).isBetween(8.0, 10.0);
    });
  }

  @Test
  void quartersTileTheParentWithoutGaps() {
    double parentArea = (PARENT.maxLat() - PARENT.minLat()) * (PARENT.maxLon() - PARENT.minLon());
    double sumOfQuarters = PARENT.quarters().stream()
        .mapToDouble(b -> (b.maxLat() - b.minLat()) * (b.maxLon() - b.minLon()))
        .sum();
    assertThat(sumOfQuarters).isCloseTo(parentArea, within(1e-9));
  }

  @Test
  void quartersMeetAtCenter() {
    List<BoundingBox> quarters = PARENT.quarters();
    assertThat(quarters).anySatisfy(b -> {
      assertThat(b.maxLat()).isEqualTo(45.0); // midpoint lat
      assertThat(b.maxLon()).isEqualTo(9.0);  // midpoint lon
    });
  }


}

package dev.stefa.forestmap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Geometry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the orchestration logic in isolation: collaborators are mocked, so no
 * network and no database. The interesting case is the quadtree subdivision.
 */
@ExtendWith(MockitoExtension.class)
class IngestionServiceTest {

  @Mock
  AdeWfsClient client;
  @Mock
  GmlParser parser;
  @Mock
  ParticellaRepository repository;
  @InjectMocks
  IngestionService service;

  @Test
  void upsertsEachParcelWhenUnderCap() throws Exception {
    when(client.maxFeatures()).thenReturn(1000);
    when(client.getFeatures(any())).thenReturn(emptyStream());
    when(parser.parse(any())).thenReturn(List.of(parcel("1"), parcel("2"), parcel("3")));

    int count = service.ingest(new BoundingBox(44, 8, 45, 9));

    assertThat(count).isEqualTo(3);
    verify(repository, times(3)).upsert(any(), any());
    verify(client, times(1)).getFeatures(any()); // no subdivision
  }

  @Test
  void subdividesWhenResponseHitsCap() throws Exception {
    when(client.maxFeatures()).thenReturn(3);
    when(client.getFeatures(any())).thenReturn(emptyStream());
    when(parser.parse(any()))
        .thenReturn(List.of(parcel("1"), parcel("2"))) // root: full page -> truncated
        .thenReturn(List.of(parcel("a")))              // 4 quarters, each under cap
        .thenReturn(List.of(parcel("b")))
        .thenReturn(List.of(parcel("c")))
        .thenReturn(List.of(parcel("d")));

    int count = service.ingest(new BoundingBox(44, 8, 46, 10));

    assertThat(count).isEqualTo(4);                  // only the leaf parcels are upserted
    verify(client, times(5)).getFeatures(any());     // 1 root + 4 quarters
    verify(repository, times(4)).upsert(any(), any());
  }

  private static ParsedParcel parcel(String numero) {
    return new ParsedParcel(new CadastralReference("H366", null, "2", numero),
        org.mockito.Mockito.mock(Geometry.class));
  }

  private static InputStream emptyStream() {
    return new ByteArrayInputStream(new byte[0]);
  }
}

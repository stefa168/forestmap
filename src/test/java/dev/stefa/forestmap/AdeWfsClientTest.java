package dev.stefa.forestmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/// Verifies the request the client builds, without touching AdE. The valuable
/// assertion is the BBOX ordering: minLat,minLon,maxLat,maxLon (EPSG:6706 axis order).
class AdeWfsClientTest {

  private MockRestServiceServer server;
  private AdeWfsClient client;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    client = new AdeWfsClient(builder, 1000);
  }

  @Test
  void buildsBboxGetFeatureRequest() throws Exception {
    server.expect(request -> {
      String uri = URLDecoder.decode(request.getURI().toString(), StandardCharsets.UTF_8);
      assertThat(uri).contains("REQUEST=GetFeature");
      assertThat(uri).contains("TYPENAMES=CP:CadastralParcel");
      assertThat(uri).contains("SRSNAME=urn:ogc:def:crs:EPSG::6706");
      // lat,lon,lat,lon — the order that matters
      assertThat(uri).contains("BBOX=44.0,8.0,45.0,9.0");
    }).andRespond(withSuccess("<wfs:FeatureCollection/>", MediaType.APPLICATION_XML));

    try (InputStream in = client.getFeatures(new BoundingBox(44.0, 8.0, 45.0, 9.0))) {
      String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      assertThat(body).contains("FeatureCollection");
    }
    server.verify();
  }
}

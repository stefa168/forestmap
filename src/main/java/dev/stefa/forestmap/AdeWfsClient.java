package dev.stefa.forestmap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Talks to the AdE cadastral WFS. The service only honours GET GetFeature with a
 * BBOX — no attribute filters, no CQL — so this client deliberately exposes
 * nothing more than a bbox fetch.
 */
@Component
public class AdeWfsClient {

    private static final String BASE =
            "https://wfs.cartografia.agenziaentrate.gov.it/inspire/wfs/owfs01.php";

    private final RestClient http;
    private final int maxFeatures;

    public AdeWfsClient(RestClient.Builder builder,
                        @Value("${cadastre.ade.max-features:1000}") int maxFeatures) {
        this.http = builder
                // The endpoint applies bot detection; a plain UA is usually enough.
                .defaultHeader("User-Agent", "cadastre-ingestion/1.0")
                .build();
        this.maxFeatures = maxFeatures;
    }

    /** Raw GML response for the given bbox. Caller is responsible for closing the stream. */
    public InputStream getFeatures(BoundingBox b) {
        byte[] body = http.get()
                .uri(BASE, uri -> uri
                        .queryParam("language", "ita")
                        .queryParam("SERVICE", "WFS")
                        .queryParam("VERSION", "2.0.0")
                        .queryParam("REQUEST", "GetFeature")
                        .queryParam("TYPENAMES", "CP:CadastralParcel")
                        .queryParam("SRSNAME", "urn:ogc:def:crs:EPSG::6706")
                        // EPSG:6706 axis order is lat,lon -> minLat,minLon,maxLat,maxLon
                        .queryParam("BBOX", "%s,%s,%s,%s".formatted(
                                b.minLat(), b.minLon(), b.maxLat(), b.maxLon()))
                        .queryParam("COUNT", maxFeatures)
                        .build())
                .retrieve()
                .body(byte[].class);
        return new ByteArrayInputStream(body == null ? new byte[0] : body);
    }

    /** The per-request feature cap; a full page signals likely truncation. */
    public int maxFeatures() {
        return maxFeatures;
    }
}

package dev.stefa.forestmap.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties("app.storage")
public record StorageProperties(
    URI internalEndpoint,   // http://s3:8333 — server-to-server
    URI publicEndpoint,     // https://storage.example.org — what the browser resolves
    String region,
    String mediaBucket,
    String quarantineBucket,
    Credentials app,
    Credentials uploader,
    Duration presignTtl
) {
  public record Credentials(String accessKey, String secretKey) {}
}
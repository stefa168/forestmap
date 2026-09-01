package dev.stefa.forestmap.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class StorageConfig {
  @Bean
  S3Client s3Client(StorageProperties props) {
    return S3Client.builder()
        .endpointOverride(props.internalEndpoint())
        .region(Region.of(props.region()))
        .credentialsProvider(staticCreds(props.app()))
        .forcePathStyle(true)
        .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
        .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
        .build();
  }

  @Bean
  S3Presigner uploadPresigner(StorageProperties props) {
    return S3Presigner.builder()
        .endpointOverride(props.publicEndpoint())   // NOT the internal one
        .region(Region.of(props.region()))
        .credentialsProvider(staticCreds(props.uploader()))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true) // ?
//            .checksumValidationEnabled(false)
            .build())
        .build();
  }

  private static StaticCredentialsProvider staticCreds(StorageProperties.Credentials c) {

    return StaticCredentialsProvider.create(
        AwsBasicCredentials.create(c.accessKey(), c.secretKey())
    );
  }
}

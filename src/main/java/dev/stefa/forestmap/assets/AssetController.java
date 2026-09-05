package dev.stefa.forestmap.assets;

import com.fasterxml.uuid.Generators;
import dev.stefa.forestmap.storage.StorageProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static software.amazon.awssdk.core.sync.RequestBody.fromBytes;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/assets")
public class AssetController {
  private final S3Client s3;
  private final S3Presigner presigner;
  private final StorageProperties props;
  private final AssetRepository assetRepository;

  // todo spostare in AssetService
  public Asset newPending(FileMeta meta) {
    var id = Generators.timeBasedEpochGenerator().generate();

    Asset a = Asset.builder()
        .id(id)
        .bucket(props.quarantineBucket())
        .contentType(meta.type())
        .sizeBytes(meta.size())
        .filename(meta.name())
        .state(Asset.AssetState.PENDING_UPLOAD)
        .build();

    assetRepository.insertPending(a);
    return a;
  }

  @PostMapping("/presign")
  public PresignedUpload presignUpload(@Validated @RequestBody FileMeta meta) {
    log.info("{}", meta);
    var asset = newPending(meta);

    var put = PutObjectRequest.builder()
        .bucket(asset.bucket())
        .key(asset.id().toString())
        .contentType(asset.contentType())
        .contentLength(asset.sizeBytes())
        .build();

    var presigned = presigner.presignPutObject(b -> b
        .signatureDuration(props.presignTtl())
        .putObjectRequest(put));

    String url = presigned.url().toString();
    log.debug("Presigned URL is {}", url);

    return new PresignedUpload(asset.id(), url, meta.type());
  }

  @Validated
  public record FileMeta(
      @NotBlank
      @Size(max = 255)
      String name,
      @Min(1)
      @Max(10 * 1024 * 1024)
      long size,
      @NotBlank
      @Pattern(regexp = "image/jpeg|image/png|application/pdf")
      String type
  ) {}

  public record PresignedUpload(UUID id, String url, String contentType) {}

  @PostMapping("/finalize")
  public ResponseEntity<Void> finalizeUpload(@Validated @RequestBody UUID id) throws URISyntaxException {
    var optAsset = assetRepository.findById(id);
    if (optAsset.isEmpty()) {
      log.warn("Received request to finalize upload for asset {} which doesn't exist in the DB", id);
      return ResponseEntity.notFound().build();
    }

    if(!assetRepository.markPendingValidation(id)) {
      log.error("`markPendingValidation` fallita per asset {}; dati DB: {}", id, optAsset.get());
      return ResponseEntity.internalServerError().build();
    }

    String objectKey = id.toString();
    ResponseBytes<GetObjectResponse> body;
    try {
      body = s3.getObjectAsBytes(r -> r.bucket(props.quarantineBucket()).key(objectKey));
    } catch (NoSuchKeyException e) {
      return ResponseEntity.notFound().build();
    }

    // todo validazione con tika o simili, magic byte, thumbhash, estrazione della dimensione con body.asByteArray()

    try {
      s3.putObject(r -> r.bucket(props.mediaBucket()).key(objectKey), fromBytes(body.asByteArray()));
    } catch (AwsServiceException | SdkClientException e) {
      log.error("Exception on PUT to asset bucket",e);
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
    s3.deleteObject(r -> r.bucket(props.quarantineBucket()).key(objectKey));

//    assetRepository.markAvailable(id, )

    URI location = s3.utilities().getUrl(r -> r.bucket(props.mediaBucket()).key(objectKey)).toURI();

/*    URI location = UriComponentsBuilder.fromUriString(props.publicBaseUrl())
        .pathSegment(objectKey)
        .build()
        .toUri();*/

    return ResponseEntity.created(location).build();
  }
}


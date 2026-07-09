package com.library.hei.storage;

import com.library.hei.PojaGenerated;
import com.library.hei.conf.BucketConf;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

/**
 * Dépose un fichier (ex : ticket PDF) sur le bucket S3 configuré et retourne un lien de
 * téléchargement temporaire (URL présignée), sans rendre le bucket public.
 */
@PojaGenerated
@Component
@AllArgsConstructor
public class TicketStorage {

  private static final Duration DOWNLOAD_LINK_VALIDITY = Duration.ofDays(7);

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final BucketConf bucketConf;

  /**
   * Envoie {@code content} sous la clé {@code key} dans le bucket configuré, puis retourne un lien
   * de téléchargement valable {@link #DOWNLOAD_LINK_VALIDITY}.
   */
  public String upload(String key, byte[] content) {
    s3Client.putObject(
        PutObjectRequest.builder()
            .bucket(bucketConf.getBucketName())
            .key(key)
            .contentType("application/pdf")
            .build(),
        RequestBody.fromBytes(content));

    return presignDownloadUrl(key);
  }

  private String presignDownloadUrl(String key) {
    var presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(DOWNLOAD_LINK_VALIDITY)
            .getObjectRequest(
                GetObjectRequest.builder().bucket(bucketConf.getBucketName()).key(key).build())
            .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}

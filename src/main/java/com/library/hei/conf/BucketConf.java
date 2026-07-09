package com.library.hei.conf;

import com.library.hei.PojaGenerated;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Configuration du bucket S3 utilisé pour stocker les fichiers générés par l'application (ex :
 * tickets de vente au format PDF).
 *
 * <p>Le nom du bucket ("aws.s3.bucket") et la région ("aws.s3.region") sont surchargeables via des
 * propriétés Spring / variables d'environnement, avec des valeurs par défaut pour le développement
 * local.
 */
@PojaGenerated
@Configuration
public class BucketConf {

  @Getter private final String bucketName;
  private final Region region;

  public BucketConf(
      @Value("${aws.s3.bucket:dummy-bucket}") String bucketName,
      @Value("${aws.s3.region:eu-west-3}") Region region) {
    this.bucketName = bucketName;
    this.region = region;
  }

  @Bean
  public S3Client getS3Client() {
    return S3Client.builder().region(region).build();
  }

  @Bean
  public S3Presigner getS3Presigner() {
    return S3Presigner.builder().region(region).build();
  }
}

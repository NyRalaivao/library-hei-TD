package com.library.hei.storage;

import com.library.hei.PojaGenerated;
import com.library.hei.file.bucket.BucketComponent;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Dépose un fichier (ex : ticket PDF) sur le bucket S3 configuré et retourne un lien de
 * téléchargement temporaire (URL présignée), sans rendre le bucket public.
 *
 * <p>Réutilise le composant {@link BucketComponent} déjà généré/testé pour l'upload et le
 * présignage, plutôt que de dupliquer une configuration S3 (voir historique : la précédente
 * implémentation redéfinissait son propre {@code BucketConf} avec un {@code @Value} sur un type
 * {@code Region}, ce que Spring ne sait pas convertir — d'où le crash au démarrage).
 */
@PojaGenerated
@Component
@AllArgsConstructor
public class TicketStorage {

  private static final Duration DOWNLOAD_LINK_VALIDITY = Duration.ofDays(7);

  private final BucketComponent bucketComponent;

  /**
   * Envoie {@code content} sous la clé {@code key} dans le bucket configuré, puis retourne un lien
   * de téléchargement valable {@link #DOWNLOAD_LINK_VALIDITY}.
   */
  public String upload(String key, byte[] content) {
    File tempFile = writeToTempFile(content);
    try {
      bucketComponent.upload(tempFile, key);
      return bucketComponent.presign(key, DOWNLOAD_LINK_VALIDITY).toString();
    } finally {
      tempFile.delete();
    }
  }

  private File writeToTempFile(byte[] content) {
    try {
      File tempFile = File.createTempFile("ticket-", ".pdf");
      Files.write(tempFile.toPath(), content);
      tempFile.deleteOnExit();
      return tempFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

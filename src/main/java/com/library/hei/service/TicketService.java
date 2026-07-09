package com.library.hei.service;

import com.library.hei.mail.Email;
import com.library.hei.mail.Mailer;
import com.library.hei.model.entity.Sale;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.pdf.TicketPdfGenerator;
import com.library.hei.repository.SaleRepository;
import com.library.hei.storage.TicketStorage;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Orchestre l'envoi du ticket de vente au client :
 *
 * <ol>
 *   <li>génère le PDF du ticket ({@link TicketPdfGenerator}),
 *   <li>le dépose sur S3 et récupère un lien de téléchargement ({@link TicketStorage}),
 *   <li>envoie un email au client (lien de téléchargement + PDF en pièce jointe) via {@link
 *       Mailer}. Cet email est envoyé automatiquement par POJA depuis l'adresse noreply configurée
 *       dans {@code EmailConf}.
 * </ol>
 */
@Service
@AllArgsConstructor
public class TicketService {

  private final SaleRepository saleRepository;
  private final TicketPdfGenerator ticketPdfGenerator;
  private final TicketStorage ticketStorage;
  private final Mailer mailer;

  /**
   * Génère et envoie par email le ticket PDF de la vente {@code saleId}.
   *
   * @return le lien de téléchargement du ticket déposé sur S3
   */
  public String sendTicket(String saleId) {
    Sale sale = getConfirmedSale(saleId);
    String customerEmail = getCustomerEmail(sale);

    byte[] pdfBytes = ticketPdfGenerator.apply(sale);
    String downloadUrl = ticketStorage.upload(ticketKey(saleId), pdfBytes);

    mailer.accept(buildEmail(customerEmail, downloadUrl, pdfBytes, saleId));

    return downloadUrl;
  }

  private Sale getConfirmedSale(String saleId) {
    Sale sale =
        saleRepository
            .findById(saleId)
            .orElseThrow(() -> new NotFoundException("Vente id=" + saleId + " introuvable"));

    if (sale.getStatus() != Sale.SaleStatus.DONE) {
      throw new BadRequestException(
          "Le ticket ne peut être envoyé que pour une vente confirmée (DONE)");
    }
    return sale;
  }

  private String getCustomerEmail(Sale sale) {
    var customer = sale.getCustomer();
    if (customer == null || customer.getEmail() == null || customer.getEmail().isBlank()) {
      throw new BadRequestException("Le client de cette vente n'a pas d'adresse email");
    }
    return customer.getEmail();
  }

  private String ticketKey(String saleId) {
    return "tickets/" + saleId + ".pdf";
  }

  private Email buildEmail(String to, String downloadUrl, byte[] pdfBytes, String saleId) {
    InternetAddress recipient = toInternetAddress(to);
    File attachment = toTempPdfFile(saleId, pdfBytes);

    String htmlBody =
        "<p>Bonjour,</p>"
            + "<p>Voici le lien pour télécharger votre ticket : "
            + "<a href=\""
            + downloadUrl
            + "\">"
            + downloadUrl
            + "</a></p>"
            + "<p>Vous trouverez également ce ticket en pièce jointe.</p>";

    return new Email(
        recipient,
        List.of(),
        List.of(),
        "Votre ticket de vente n° " + saleId,
        htmlBody,
        List.of(attachment));
  }

  private InternetAddress toInternetAddress(String email) {
    try {
      return new InternetAddress(email);
    } catch (AddressException e) {
      throw new BadRequestException("Adresse email du client invalide : " + email);
    }
  }

  private File toTempPdfFile(String saleId, byte[] pdfBytes) {
    try {
      File tempFile = File.createTempFile("ticket-" + saleId, ".pdf");
      Files.write(tempFile.toPath(), pdfBytes);
      tempFile.deleteOnExit();
      return tempFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}

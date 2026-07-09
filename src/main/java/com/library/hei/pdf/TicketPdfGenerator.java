package com.library.hei.pdf;

import com.library.hei.PojaGenerated;
import com.library.hei.model.entity.Sale;
import com.library.hei.model.entity.SaleDetail;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

/**
 * Génère un ticket de vente au format PDF à partir d'une {@link Sale}.
 *
 * <p>Le contenu reste volontairement simple (résumé lisible : client, vendeur, date, lignes de
 * vente, total) : l'essentiel de la fonctionnalité est le circuit complet génération -> stockage S3
 * -> envoi par email, un contenu minimal (voire un PDF vide en cas de vente sans détail) n'est pas
 * bloquant.
 */
@PojaGenerated
@Component
public class TicketPdfGenerator implements Function<Sale, byte[]> {

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  private static final float LEFT_MARGIN = 50f;
  private static final float TOP_MARGIN = 60f;

  @Override
  public byte[] apply(Sale sale) {
    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      try (PDPageContentStream content = new PDPageContentStream(document, page)) {
        writeContent(content, sale, page.getMediaBox().getHeight() - TOP_MARGIN);
      }

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      document.save(outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void writeContent(PDPageContentStream content, Sale sale, float startY)
      throws IOException {
    float y = startY;

    y = writeLine(content, y, PDType1Font.HELVETICA_BOLD, 16, "Ticket de vente");
    y -= 10;
    y = writeLine(content, y, PDType1Font.HELVETICA, 11, "Vente n° " + safe(sale.getId()));
    y = writeLine(content, y, PDType1Font.HELVETICA, 11, "Date : " + formatDate(sale));
    y = writeLine(content, y, PDType1Font.HELVETICA, 11, "Client : " + customerName(sale));
    y = writeLine(content, y, PDType1Font.HELVETICA, 11, "Vendeur : " + sellerName(sale));
    y -= 10;

    for (SaleDetail detail : sale.getSaleDetails()) {
      String line =
          String.format(
              "%-35s x%-4d %12s",
              bookTitle(detail), quantity(detail), safe(detail.getTotalPrice()));
      y = writeLine(content, y, PDType1Font.HELVETICA, 10, line);
    }

    y -= 10;
    writeLine(
        content,
        y,
        PDType1Font.HELVETICA_BOLD,
        12,
        "Total : " + safe(sale.getTotalAmount()) + " Ar");
  }

  private float writeLine(
      PDPageContentStream content, float y, PDType1Font font, int fontSize, String text)
      throws IOException {
    content.beginText();
    content.setFont(font, fontSize);
    content.newLineAtOffset(LEFT_MARGIN, y);
    content.showText(text == null ? "" : text);
    content.endText();
    return y - (fontSize + 6);
  }

  private String formatDate(Sale sale) {
    return sale.getSaleDate() == null ? "" : sale.getSaleDate().format(DATE_FORMAT);
  }

  private String customerName(Sale sale) {
    var customer = sale.getCustomer();
    if (customer == null) return "";
    return safe(customer.getFirstName()) + " " + safe(customer.getLastName());
  }

  private String sellerName(Sale sale) {
    var seller = sale.getSeller();
    return seller == null ? "" : safe(seller.getUsername());
  }

  private String bookTitle(SaleDetail detail) {
    var format = detail.getBookFormat();
    if (format == null || format.getBook() == null) return "";
    return safe(format.getBook().getTitle());
  }

  private int quantity(SaleDetail detail) {
    return detail.getQuantity() == null ? 0 : detail.getQuantity();
  }

  private String safe(Object value) {
    return value == null ? "" : value.toString();
  }
}

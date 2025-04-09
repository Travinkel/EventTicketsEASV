package org.example.eventticketsystem.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.example.eventticketsystem.dal.models.Event;
import org.example.eventticketsystem.dal.models.Ticket;
import org.example.eventticketsystem.utils.QRGenerator;
import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Scope;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Injectable
@Scope("singleton")
public class PDFGenerator {

    public static String generate(Ticket ticket, Event event) {
        String filename = "ticket_" + ticket.getId() + "_" + UUID.randomUUID() + ".pdf";
        String path = System.getProperty("java.io.tmpdir") + File.separator + filename;

        try {
            Document document = new Document(PageSize.A6.rotate(), 30, 30, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Background
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle bg = new Rectangle(document.getPageSize());
            bg.setBackgroundColor(new Color(245, 245, 245)); // Light gray
            canvas.rectangle(bg);

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY);

            // Title
            Paragraph header = new Paragraph("🎓 EASV Student Bar – Billet", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(15);
            document.add(header);

            // Ticket Info Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            table.addCell(createCell("Billet ID:", labelFont));
            table.addCell(createCell(String.valueOf(ticket.getId()), valueFont));

            table.addCell(createCell("Event:", labelFont));
            table.addCell(createCell(event.getTitle(), valueFont));

            table.addCell(createCell("Dato:", labelFont));
            table.addCell(createCell(event.getStartTime()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy")), valueFont));

            table.addCell(createCell("Bruger ID:", labelFont));
            table.addCell(createCell(String.valueOf(ticket.getUserId()), valueFont));

            table.addCell(createCell("Pris:", labelFont));
            table.addCell(createCell(String.format("%.2f DKK", ticket.getPriceAtPurchase()), valueFont));

            document.add(table);

            // QR code (now using QRGenerator)
            Image qrImg = QRGenerator.generateQRCodeImage(ticket.getQrCode(), 100, 100);
            qrImg.setAlignment(Image.ALIGN_CENTER);
            qrImg.scaleToFit(80, 80);
            qrImg.setSpacingBefore(15);
            document.add(qrImg);

            // Barcode (Code128)
            Barcode128 barcode = new Barcode128();
            barcode.setCode(ticket.getBarcode());
            barcode.setCodeType(Barcode.CODE128);
            Image barcodeImg = barcode.createImageWithBarcode(canvas, null, null);
            barcodeImg.setAlignment(Image.ALIGN_CENTER);
            barcodeImg.scaleToFit(130, 50);
            barcodeImg.setSpacingBefore(10);
            document.add(barcodeImg);

            // Footer
            Paragraph footer = new Paragraph("Vis denne billet ved indgangen – god fornøjelse!", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    private static PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
}

package org.example.eventticketsystem.utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.example.eventticketsystem.models.Ticket;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    public static String generateTicketPDF(Ticket ticket, String outputDir) {
        String filePath = outputDir + "/ticket_" + ticket.getId() + ".pdf";

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("EASV Event Ticket", titleFont));
            document.add(new Paragraph("Ticket ID: " + ticket.getId(), bodyFont));
            document.add(new Paragraph("Event ID: " + ticket.getEventId(), bodyFont));
            document.add(new Paragraph("User ID: " + ticket.getUserId(), bodyFont));
            document.add(new Paragraph("Issued At: " + ticket.getIssuedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), bodyFont));

            document.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return null;
        }

        return filePath;
    }
}
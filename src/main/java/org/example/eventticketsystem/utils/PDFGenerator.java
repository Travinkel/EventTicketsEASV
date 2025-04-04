package org.example.eventticketsystem.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.example.eventticketsystem.models.Ticket;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    /**
     * Generates a business-card–inspired PDF ticket.
     *
     * @param ticket    the ticket object containing details
     * @param outputDir directory where the PDF will be saved
     * @return the path to the generated PDF file or null on error
     */
    public static String generateEASVTicketPDF(Ticket ticket, String outputDir) {
        String filePath = outputDir + "/ticket_" + ticket.getId() + ".pdf";
        try {
            // Set up the document in A6 (rotated) with refined margins
            Document document = new Document(PageSize.A6, 20, 20, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Colors using com.lowagie.text.Color
            Color backgroundColor = new Color(250, 250, 250); // off-white (cheap to print)
            Color borderColor = new Color(200, 200, 200);     // subtle gray
            Color titleColor = new Color(30, 30, 30);          // dark text
            Color detailColor = new Color(80, 80, 80);          // medium dark text

            // Draw background with a subtle border
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = new Rectangle(document.getPageSize());
            rect.setBackgroundColor(backgroundColor);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(2);
            rect.setBorderColor(borderColor);
            canvas.rectangle(rect);

            // Create fonts (using Times for an elegant feel)
            BaseFont baseSerif = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(baseSerif, 16, Font.BOLD, titleColor);
            Font detailFont = new Font(baseSerif, 10, Font.NORMAL, detailColor);
            Font smallFont = new Font(baseSerif, 8, Font.ITALIC, detailColor);

            // Header (centered title)
            Paragraph header = new Paragraph("EASV Ticket", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(10);
            document.add(header);

            // Ticket Details Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(10);

            table.addCell(createCell("Ticket ID:", detailFont));
            table.addCell(createCell(String.valueOf(ticket.getId()), detailFont));

            table.addCell(createCell("Event ID:", detailFont));
            table.addCell(createCell(String.valueOf(ticket.getEventId()), detailFont));

            table.addCell(createCell("User ID:", detailFont));
            table.addCell(createCell(String.valueOf(ticket.getUserId()), detailFont));

            table.addCell(createCell("Issued At:", detailFont));
            table.addCell(createCell(ticket.getIssuedAt()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")), detailFont));

            // Separator line
            PdfPCell separator = new PdfPCell(new Phrase(""));
            separator.setColspan(2);
            separator.setBorder(Rectangle.BOTTOM);
            separator.setBorderColor(borderColor);
            separator.setPaddingBottom(5);
            table.addCell(separator);

            // QR code placeholder (for future implementation)
            PdfPCell qrCell = new PdfPCell(new Phrase("🔳 QR Code Coming Soon", detailFont));
            qrCell.setColspan(2);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setBorder(Rectangle.NO_BORDER);
            qrCell.setPaddingTop(10);
            table.addCell(qrCell);

            document.add(table);

            // Footer note
            Paragraph footer = new Paragraph("Enjoy your event – EASV Student Bar", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            document.add(footer);

            document.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }


    public static String generateTicketPDF(Ticket ticket, String outputDir) {
        String filePath = outputDir + "/ticket_" + ticket.getId() + ".pdf";

        try {
            Document document = new Document(PageSize.A6.rotate(), 30, 30, 20, 20);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            Rectangle background = new Rectangle(document.getPageSize());
            background.setBackgroundColor(new Color(30, 30, 30)); // deep gray
            canvas.rectangle(background);

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.LIGHT_GRAY);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);

            // Header
            Paragraph header = new Paragraph("🎓 EASV Student Bar", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);

            // Ticket Info Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.setSpacingBefore(10);

            table.addCell(createCell("🎟️ Ticket ID:", labelFont));
            table.addCell(createCell(String.valueOf(ticket.getId()), valueFont));

            table.addCell(createCell("📅 Event ID:", labelFont));
            table.addCell(createCell(String.valueOf(ticket.getEventId()), valueFont));

            table.addCell(createCell("🧑 User ID:", labelFont));
            table.addCell(createCell(String.valueOf(ticket.getUserId()), valueFont));

            table.addCell(createCell("⏰ Issued At:", labelFont));
            table.addCell(createCell(ticket.getIssuedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")), valueFont));

            PdfPCell qrCell = new PdfPCell(new Phrase("🔳 QR Code Coming Soon", valueFont));
            qrCell.setColspan(2);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setBorder(Rectangle.NO_BORDER);
            qrCell.setPaddingTop(10);
            table.addCell(qrCell);

            document.add(table);

            Paragraph footer = new Paragraph("Vis denne billet ved indgangen – god fornøjelse!", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(25);
            document.add(footer);

            document.close();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return null;
        }

        return filePath;
    }

    public static String generateElegantMockTicket(Ticket ticket, String outputDir) {
        String filePath = outputDir + "/easv_ticket_" + ticket.getId() + ".pdf";

        try {
            Document document = new Document(PageSize.A6.rotate(), 30, 30, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseFont baseSerif = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            Font bigNameFont = new Font(baseSerif, 16, Font.BOLD);
            Font roleFont = new Font(baseSerif, 12, Font.ITALIC);
            Font smallFont = new Font(baseSerif, 10);

            // Top Left
            Paragraph phone = new Paragraph("+45 42 69 420", smallFont);
            phone.setAlignment(Element.ALIGN_LEFT);
            document.add(phone);

            // Top Right
            Paragraph firm = new Paragraph("EASV & EASV\nMergers and Mojitos", smallFont);
            firm.setAlignment(Element.ALIGN_RIGHT);
            document.add(firm);

            // Spacer
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Name + Title
            Paragraph name = new Paragraph("Patrick Pilsner", bigNameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);

            Paragraph title = new Paragraph("Vice Drinker", roleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Event
            document.add(Chunk.NEWLINE);
            Paragraph event = new Paragraph("Event: The Sinking Ship (04 Apr 2025)", smallFont);
            event.setAlignment(Element.ALIGN_CENTER);
            document.add(event);

            // Spacer
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Footer
            Paragraph address = new Paragraph("358 H.C. Ørstedvej, Esbjerg, DK 6700   FAX +45 42 69 420   TELEX 10 4534", smallFont);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return filePath;
    }


    private static PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }
}

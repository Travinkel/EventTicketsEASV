package org.example.eventticketsystem.bll;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import org.example.eventticketsystem.EventTicketSystemApp;
import org.example.eventticketsystem.models.Ticket;
import org.example.eventticketsystem.utils.Config;

import java.io.File;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventTicketSystemApp.class);

    private final Session session;
    private final String from;

    public EmailService() {
        String host = Config.get("email.host");
        String port = Config.get("email.port");
        String username = Config.get("email.username");
        String password = Config.get("email.password");
        this.from = Config.get("email.from");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        this.session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }


    public boolean sendEmailWithAttachment(String to, String subject, String body, String filePath) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(filePath));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendTicketToUser(int userId, Ticket ticket) {
        // Simulate sending
        LOGGER.info("📧 Sending ticket to user ID {} with QR: {}", userId, ticket.getQrCode());
    }
}
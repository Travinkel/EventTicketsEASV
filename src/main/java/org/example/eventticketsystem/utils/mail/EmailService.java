package org.example.eventticketsystem.utils.mail;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.example.eventticketsystem.utils.Config;
import org.example.eventticketsystem.utils.di.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final Session session;
    private final String from;

    public EmailService(Config config) {
        String host = config.get(Config.Key.EMAIL_HOST);
        String port = config.get(Config.Key.EMAIL_PORT);
        String username = config.get(Config.Key.EMAIL_USERNAME);
        String password = config.get(Config.Key.EMAIL_PASSWORD);
        this.from = config.get(Config.Key.EMAIL_FROM);

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


    public boolean sendEmailWithAttachment(String to, String subject, String body, byte[] fileContent,
                                           String filename) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setFileName(filename);
            attachmentPart.setContent(fileContent, "application/pdf");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);
            return true;

        } catch (Exception e) {
            LOGGER.error("❌ Failed to send email with attachment", e);
            return false;
        }
    }

}

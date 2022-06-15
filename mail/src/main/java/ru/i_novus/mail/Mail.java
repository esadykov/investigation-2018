package ru.i_novus.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Mail {
    public static void main(String[] args) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", false);
        prop.put("mail.smtp.host", "maildev.i-novus.ru");
        prop.put("mail.smtp.port", "1025");
//        prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");


        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("someuser", "somepass");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("test@test.test"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("mail@mail.mu"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}

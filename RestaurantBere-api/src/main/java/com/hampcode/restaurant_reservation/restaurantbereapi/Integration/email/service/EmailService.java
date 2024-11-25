package com.hampcode.restaurant_reservation.restaurantbereapi.Integration.email.service;

import com.hampcode.restaurant_reservation.restaurantbereapi.Integration.email.dto.Mail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public Mail createMail(String to, String subject, @Nullable String[] bccRecipients, Map<String, Object> model) {
        Mail mail = new Mail();
        mail.setFrom("govench6@gmail.com");
        mail.setTo(to);
        mail.setBcc(bccRecipients != null ? bccRecipients : new String[0]);
        mail.setSubject(subject);
        mail.setModel(model);
        return mail;
    }

    public void sendEmail(Mail mail, String templateName) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        Context context = new Context();
        context.setVariables(mail.getModel());

        String html = templateEngine.process(templateName, context);
        helper.setTo(mail.getTo());
        helper.setText(html, true);
        helper.setBcc(mail.getBcc());
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        mailSender.send(message);

    }

}

package org.example.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class EmailService {
    private final ObjectProvider<JavaMailSender> senders;
    @Value("${app.email.enabled:false}") private boolean enabled;
    @Value("${spring.mail.username:}") private String from;

    public void sendExpiryWarning(String recipient, String message) {
        if (!enabled) return;
        var sender = senders.getIfAvailable();
        if (sender == null) {
            log.warn("Email enabled but no mail server configured");
            return;
        }
        try {
            var mail = new SimpleMailMessage();
            if (!from.isBlank()) mail.setFrom(from);
            mail.setTo(recipient);
            mail.setSubject("MealWheel: thực phẩm sắp hết hạn");
            mail.setText(message);
            sender.send(mail);
        } catch (org.springframework.mail.MailException ex) {
            log.warn("Could not send expiry email ({})", ex.getClass().getSimpleName());
        }
    }
    public void sendAuthenticationCode(String recipient, String subject, String content) {
        var sender = senders.getIfAvailable();
        if (!enabled || sender == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "Email delivery is not configured. Enable EMAIL_ENABLED and configure mail settings");
        }
        var mail = new SimpleMailMessage();
        if (!from.isBlank()) mail.setFrom(from);
        mail.setTo(recipient);
        mail.setSubject(subject);
        mail.setText(content);
        try { sender.send(mail); }
        catch (org.springframework.mail.MailException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to send verification email. Please try again later");
        }
    }
}

package secretstuffs.application.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String recipientEmail, String verificationLink) {
        try {
            String stringifiedTemplate = loadEmailTemplate("Templates/verification-email.html");
            String emailContent = stringifiedTemplate.replace("{verificationLink}", verificationLink);
            sendEmail(recipientEmail, "Verify your email address", emailContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendForgotPasswordEmail(String recipientEmail, String resetLink) {
        try {
            String emailContent = loadEmailTemplate("Templates/forgot-password-email.html")
                    .replace("{resetLink}", resetLink);

            sendEmail(recipientEmail, "Reset your password", emailContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send forgot password email", e);
        }
    }

    public String loadEmailTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private void sendEmail(String recipientEmail, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}

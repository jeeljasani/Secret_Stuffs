package secretstuffs.application.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendVerificationEmail_ShouldSendEmailWithCorrectContent() {
        String recipientEmail = "test@example.com";
        String verificationLink = "http://example.com/verify";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail(recipientEmail, verificationLink);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendForgotPasswordEmail_ShouldSendEmailWithCorrectContent() {
        String recipientEmail = "test@example.com";
        String resetLink = "http://example.com/reset";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendForgotPasswordEmail(recipientEmail, resetLink);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void sendVerificationEmail_ShouldThrowRuntimeException_WhenIOExceptionOccurs() throws IOException {
        String recipientEmail = "test@example.com";
        String verificationLink = "http://example.com/verify";

        // Spy on the service to mock private method behavior
        EmailService spyService = spy(emailService);
        IOException ex = new IOException("Template file not found");
        String path = "Templates/verification-email.html";
        // Simulate IOException in loadEmailTemplate
        doThrow(ex).when(spyService).loadEmailTemplate(path);

        // Assert that the RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spyService.sendVerificationEmail(recipientEmail, verificationLink);
        });

        assert exception.getMessage().contains("Failed to load email template");
    }

    @Test
    void sendForgotPasswordEmail_ShouldThrowRuntimeException_WhenIOExceptionOccurs() throws IOException {
        String recipientEmail = "test@example.com";
        String resetLink = "http://example.com/reset";

        // Spy on the service to mock private method behavior
        EmailService spyService = spy(emailService);

        IOException ex = new IOException("Template file not found");
        String path = "Templates/forgot-password-email.html";
        // Simulate IOException in loadEmailTemplate
        doThrow(ex).when(spyService).loadEmailTemplate(path);

        // Assert that the RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            spyService.sendForgotPasswordEmail(recipientEmail, resetLink);
        });

        assert exception.getMessage().contains("Failed to load email template");
    }
}
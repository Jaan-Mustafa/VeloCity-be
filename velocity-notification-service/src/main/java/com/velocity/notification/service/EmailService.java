package com.velocity.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.velocity.notification.model.dto.EmailRequest;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending email notifications.
 * Uses Spring Mail with MailHog for development.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${app.notification.from-email}")
    private String fromEmail;
    
    /**
     * Send email asynchronously
     */
    @Async
    public void sendEmail(EmailRequest request) {
        try {
            log.info("Sending email to: {}, subject: {}", request.getTo(), request.getSubject());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), request.getIsHtml());
            
            mailSender.send(message);
            
            log.info("Email sent successfully to: {}", request.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}, error: {}", request.getTo(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    /**
     * Send welcome email
     */
    public void sendWelcomeEmail(String to, String userName) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject("Welcome to VeloCity!")
                .body(buildWelcomeEmailBody(userName))
                .isHtml(true)
                .build();
        
        sendEmail(request);
    }
    
    /**
     * Send ride confirmation email
     */
    public void sendRideConfirmationEmail(String to, String userName, Long rideId) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject("Ride Confirmed - VeloCity")
                .body(buildRideConfirmationBody(userName, rideId))
                .isHtml(true)
                .build();
        
        sendEmail(request);
    }
    
    /**
     * Send ride completion email
     */
    public void sendRideCompletionEmail(String to, String userName, Long rideId, String fare) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject("Ride Completed - VeloCity")
                .body(buildRideCompletionBody(userName, rideId, fare))
                .isHtml(true)
                .build();
        
        sendEmail(request);
    }
    
    /**
     * Send payment confirmation email
     */
    public void sendPaymentConfirmationEmail(String to, String userName, String amount) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject("Payment Successful - VeloCity")
                .body(buildPaymentConfirmationBody(userName, amount))
                .isHtml(true)
                .build();
        
        sendEmail(request);
    }
    
    // Email body builders
    
    private String buildWelcomeEmailBody(String userName) {
        return String.format("""
                <html>
                <body>
                    <h2>Welcome to VeloCity, %s!</h2>
                    <p>Thank you for registering with VeloCity. We're excited to have you on board!</p>
                    <p>You can now book rides and enjoy seamless transportation.</p>
                    <br>
                    <p>Best regards,<br>The VeloCity Team</p>
                </body>
                </html>
                """, userName);
    }
    
    private String buildRideConfirmationBody(String userName, Long rideId) {
        return String.format("""
                <html>
                <body>
                    <h2>Ride Confirmed!</h2>
                    <p>Hi %s,</p>
                    <p>Your ride (ID: %d) has been confirmed. Your driver is on the way!</p>
                    <p>Track your ride in the VeloCity app.</p>
                    <br>
                    <p>Safe travels,<br>The VeloCity Team</p>
                </body>
                </html>
                """, userName, rideId);
    }
    
    private String buildRideCompletionBody(String userName, Long rideId, String fare) {
        return String.format("""
                <html>
                <body>
                    <h2>Ride Completed</h2>
                    <p>Hi %s,</p>
                    <p>Your ride (ID: %d) has been completed successfully.</p>
                    <p>Fare: ₹%s</p>
                    <p>Thank you for choosing VeloCity!</p>
                    <br>
                    <p>Best regards,<br>The VeloCity Team</p>
                </body>
                </html>
                """, userName, rideId, fare);
    }
    
    private String buildPaymentConfirmationBody(String userName, String amount) {
        return String.format("""
                <html>
                <body>
                    <h2>Payment Successful</h2>
                    <p>Hi %s,</p>
                    <p>Your payment of ₹%s has been processed successfully.</p>
                    <p>Your wallet has been updated.</p>
                    <br>
                    <p>Thank you,<br>The VeloCity Team</p>
                </body>
                </html>
                """, userName, amount);
    }
}

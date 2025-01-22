package com.security.spring_security.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service // Marks this class as a Spring service, making it a candidate for component scanning and dependency injection
public class EmailService {
    private final JavaMailSender mailSender; // JavaMailSender is used to send emails

    @Autowired // Injects the JavaMailSender bean into the constructor
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Sends an email with the specified recipient, subject, and message body
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); // Creates a simple email message
        message.setTo(to); // Sets the recipient of the email
        message.setSubject(subject); // Sets the subject of the email
        message.setText(text); // Sets the body of the email

        mailSender.send(message); // Sends the email using JavaMailSender
    }
}

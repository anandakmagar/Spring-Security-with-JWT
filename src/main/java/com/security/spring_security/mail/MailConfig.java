package com.security.spring_security.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration // Marks this class as a configuration class, allowing Spring to recognize and process it for bean definitions
public class MailConfig {

    @Bean // Indicates that this method returns a bean to be managed by the Spring container
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); // Creates an instance of JavaMailSenderImpl
        mailSender.setHost("smtp.gmail.com"); // Sets the SMTP host (Gmail in this case)
        mailSender.setPort(587); // Sets the SMTP port (587 for Gmail with STARTTLS)
        mailSender.setUsername("gsmtp60@gmail.com"); // Sets the email username for authentication
        mailSender.setPassword("ielsuxexelfphgoi"); // Sets the email password or app-specific password for authentication

        Properties props = mailSender.getJavaMailProperties(); // Retrieves mail properties
        props.put("mail.smtp.starttls.enable", "true"); // Enables STARTTLS for secure email transmission
        props.put("mail.smtp.auth", "true"); // Enables SMTP authentication

        return mailSender; // Returns the configured JavaMailSenderImpl instance
    }
}

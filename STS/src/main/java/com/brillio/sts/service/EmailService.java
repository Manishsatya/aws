package com.brillio.sts.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
 
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService {

	@Autowired
    private JavaMailSender javaMailSender;
 
	
    public boolean sendEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully to: " + toEmail);
            return true; // Email sent successfully
        } catch (MessagingException e) {
            System.out.println("❌ Email failed to send to: " + toEmail);
            e.printStackTrace();
            return false; //  Email failed to send
        }
    }
 
}
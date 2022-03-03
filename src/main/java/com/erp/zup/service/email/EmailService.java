package com.erp.zup.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    @Value("${application.mail.default.sender}")
    public String sender;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMails(String message,String subject, List<String> mailsList) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setTo(mailsList.stream().toArray(String[]::new));

        javaMailSender.send(mailMessage);
    }
}

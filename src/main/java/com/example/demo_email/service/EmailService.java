package com.example.demo_email.service;

import com.example.demo_email.model.EmailLog;
import com.example.demo_email.repository.EmailLogRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    public void sendEmail(String recipient, String subject, String content, String emailType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, true);  // 使用 HTML 格式

            mailSender.send(message);

            // 發信成功後紀錄到資料表
            EmailLog log = new EmailLog();
            log.setRecipient(recipient);
            log.setSubject(subject);
            log.setEmailType(emailType);
            log.setSuccess(true);
            log.setSentAt(Timestamp.valueOf(LocalDateTime.now()));
            emailLogRepository.save(log);

        } catch (Exception e) {
            // 如果發信失敗，也可以記錄到資料庫
            EmailLog log = new EmailLog();
            log.setRecipient(recipient);
            log.setSubject(subject);
            log.setEmailType(emailType);
            log.setSuccess(false);
            log.setSentAt(Timestamp.valueOf(LocalDateTime.now()));
            emailLogRepository.save(log);
        }
    }
}

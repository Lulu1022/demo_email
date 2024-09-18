package com.example.demo_email.controller;

import com.example.demo_email.dto.EmailRequest;
import com.example.demo_email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        String content = "";  // 根據 emailType 生成不同的信件內容
        String subject = "";

        try {
            switch (emailRequest.getEmailType()) {
                case "GROUP_CONFIRM":
                    subject = "成團通知: " + emailRequest.getGroupName();
                    content = loadTemplate("classpath:templates/group-confirmation.html", emailRequest.getGroupName(), emailRequest.getGroupMembers());
                    break;
                case "GROUP_CANCEL":
                    subject = "流團通知: " + emailRequest.getGroupName();
                    content = loadTemplate("classpath:templates/group-cancellation.html", emailRequest.getGroupName(), emailRequest.getGroupMembers());
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid email type");
            }

            emailService.sendEmail(emailRequest.getRecipient(), subject, content, emailRequest.getEmailType());
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error sending email");
        }
    }

    private String loadTemplate(String path, String primaryValue, List<String> secondaryValues) throws Exception {
        Resource resource = resourceLoader.getResource(path);
        String template = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // 替換 primaryValue
        template = template.replace("{{ primaryValue }}", primaryValue);

        // 動態生成團員表格的 HTML
        StringBuilder membersTable = new StringBuilder();
        for (String member : secondaryValues) {
            membersTable.append("<tr><td>").append(member).append("</td></tr>");
        }

        // 替換 secondaryValues，插入生成的表格
        template = template.replace("{{ secondaryValues }}", membersTable.toString());

        return template;
    }


}




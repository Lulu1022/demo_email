package com.example.demo_email.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String recipient;
    private String emailType;  // GROUP_CONFIRM, GROUP_CANCEL
    private String groupName;
    private List<String> groupMembers;
    private String orderNumber;
    private List<String> orderItems;

}
package com.erp.zup.service.email;

import java.util.List;

public interface IEmailService {
    void sendMails(String message,String subject, List<String> mailsList);
}

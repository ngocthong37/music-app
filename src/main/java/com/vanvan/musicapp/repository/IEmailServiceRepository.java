package com.vanvan.musicapp.repository;

import java.util.Map;

public interface IEmailServiceRepository {
    String sendMail(String to, String[] cc, String subject, Map<String, Object> model);
}

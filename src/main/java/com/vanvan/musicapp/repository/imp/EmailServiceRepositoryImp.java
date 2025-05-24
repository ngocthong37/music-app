package com.vanvan.musicapp.repository.imp;

import com.vanvan.musicapp.repository.IEmailServiceRepository;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

import java.util.Map;

@Transactional
@Repository
public class EmailServiceRepositoryImp implements IEmailServiceRepository {

    private String fromEmail = "ngocthong2k2@gmail.com";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration config;

    @Override
    public String sendMail(String to, String[] cc, String subject, Map<String, Object> model) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

//            mimeMessageHelper.addAttachment("logo.jpg", new ClassPathResource("logo.jpg"));
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(subject);
            Template t = null;
            if (subject.equals("Password Reset Request")) {
                t = config.getTemplate("forgot_password.ftl");
            }
            else if (subject.equals("Verify Account Request")) {
                t = config.getTemplate("verify-account.ftl");
            }
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

            mimeMessageHelper.setText(html, true);
            javaMailSender.send(mimeMessage);

            return "mail send";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

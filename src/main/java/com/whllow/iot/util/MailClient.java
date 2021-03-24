package com.whllow.iot.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

//    private static final Logger logger = (Logger) LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public int sendMail(String context,String subject,String to) {

        try {
            MimeMessage mess = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mess);
            helper.setTo(to);
            helper.setText(context,true);
            helper.setSubject(subject);
            helper.setFrom(from);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }



}

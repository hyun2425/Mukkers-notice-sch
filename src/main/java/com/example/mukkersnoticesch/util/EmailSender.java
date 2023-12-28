package com.example.mukkersnoticesch.util;

import java.util.Map;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static String send(String toEmail, String title, String content) {
        // 이메일 발송 관련 정보 설정
        String from = "gustkdwn2@naver.com";
        final String username = "gustkdwn2@naver.com";
        final String password = "x13101310!";

        // SMTP 서버 설정
        String host = "smtp.naver.com";

        // 메일 속성 설정
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        // 세션 생성 및 인증
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // 메시지 생성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(title);

            // 메일 내용 설정
            message.setText(content);

            // 메일 전송
            Transport.send(message);

            return "success";

        } catch (MessagingException e) {
            e.printStackTrace();
            return "fail";
        }
    }
}


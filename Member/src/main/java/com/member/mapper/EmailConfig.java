package com.member.mapper;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:application.properties")
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String id;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;

    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host); // smtp 서버 주소
        javaMailSender.setUsername(id); // 설정(발신) 메일 아이디
        javaMailSender.setPassword(password); // 설정(발신) 메일 패스워드
        javaMailSender.setPort(port); //smtp port
        javaMailSender.setJavaMailProperties(getMailProperties()); // 메일 인증서버 정보 가져온다.
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties prop =System.getProperties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true"); 
        prop.put("mail.smtp.port", port); // smtp 인증                      
        prop.put("mail.smtp.host", host); // smtp 인증)
        prop.put("mail.smtp.ssl.trust",host); // smtp 인증)
        prop.put("mail.smtp.ssl.enable","true"); // ssl 사용        
        return prop;
    }
}
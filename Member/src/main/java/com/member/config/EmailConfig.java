package com.member.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:application.properties") // Property의 값을 가져오기 위한 것 Configuration과 함께 사용된다.
public class EmailConfig {

    @Value("${spring.mail.username}") // application.propertie에서 설정해놓은 값을 불러온다 
    private String id;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")   
    private int port;

    @Bean
    public JavaMailSender javaMailService() { //Java의 메일을 전송시키기위한 API를 Bean으로 구성하여 설정값을 셋팅
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host); // SMTP서버의 주소를 설정 = 465 
        javaMailSender.setUsername(id); // 설정(발신) 메일 아이디
        javaMailSender.setPassword(password); // 설정(발신) 메일 패스워드
        javaMailSender.setPort(port); //smtp port
        javaMailSender.setJavaMailProperties(getMailProperties()); // 메일을 보내기 위해 인증서버의 설정값을 가져온다
        javaMailSender.setDefaultEncoding("UTF-8"); // 기본 인코딩 타입 설정
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties prop =System.getProperties(); // System.getProperties = 현재 위치나 시스템(운영체제(OS)나 JVM 에 의존적인 정보)를 가져올때 사용
        										// Properties =  Key=Value형식으로 파라미터 정보들을 저장하기 위한 파일 확장자
        										// 주로 응용 프로그램에 대한 환경설정정보, DB와 연결하기 위한 DB환경설정정보 등을 저장할때 사용
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        /*prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true"); 
        prop.put("mail.smtp.port", port); // smtp 인증                      
        prop.put("mail.smtp.host", host); // smtp 인증)
        prop.put("mail.smtp.ssl.trust",host); // smtp 인증)
        prop.put("mail.smtp.ssl.enable","true"); // ssl 사용
        */        
        return prop;
    }
}
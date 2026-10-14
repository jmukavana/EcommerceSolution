package com.jamlsolution.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Properties;

@Configuration
@EnableAsync
public class EmailConfig {

    @Value("${app.email.host}")
    private String host;

    @Value("${app.email.port}")
    private int port;

    @Value("${app.email.username}")
    private String username;

    @Value("${app.email.password}")
    private String password;

    @Value("${app.email.ssl-trust}")
    private String sslTrust;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol("smtps");
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.ssl.trust", sslTrust);
        props.put("mail.smtps.ssl.protocols", "TLSv1.2");
        props.put("mail.smtps.timeout", "10000");
        props.put("mail.smtps.connectiontimeout", "10000");
        props.put("mail.smtps.writetimeout", "10000");
        props.put("mail.mime.charset", "UTF-8");
        props.put("mail.debug", "false");

        return mailSender;
    }
}

package br.com.legalconnect.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // Injetar o TemplateEngine do Thymeleaf

    // Método para enviar e-mail sem template Thymeleaf (HTML direto)
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // O 'true' indica que o conteúdo é HTML

        mailSender.send(message);
    }

    // Método para enviar e-mail com template Thymeleaf
    public void sendTemplatedEmail(String to, String subject, String templateName,
            Map<String, Object> templateVariables) throws MessagingException {
        // Criar um contexto Thymeleaf e adicionar as variáveis
        Context context = new Context();
        context.setVariables(templateVariables);

        // Processar o template Thymeleaf
        String htmlContent = templateEngine.process(templateName, context);

        sendHtmlEmail(to, subject, htmlContent);
    }
}
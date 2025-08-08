package br.com.legalconnect.commom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Serviço simulado para envio de e-mails.
 * Em um ambiente real, esta classe conteria a lógica de comunicação com um provedor de e-mails
 * como SendGrid, Amazon SES, ou o serviço de e-mail do Spring Boot.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Simula o envio de um e-mail de ativação de conta.
     *
     * @param toEmail O endereço de e-mail do destinatário.
     * @param activationLink O link de ativação da conta.
     */
    public void sendActivationEmail(String toEmail, String activationLink) {
        log.info("Simulando envio de e-mail de ativação para: {}", toEmail);
        log.info("Link de Ativação: {}", activationLink);
        // Lógica real de envio de e-mail seria aqui, por exemplo:
        // MimeMessage message = mailSender.createMimeMessage();
        // MimeMessageHelper helper = new MimeMessageHelper(message, true);
        // helper.setTo(toEmail);
        // helper.setSubject("Ative sua conta na LegalConnect!");
        // helper.setText("Olá! Clique no link para ativar sua conta: " + activationLink, true);
        // mailSender.send(message);
        log.info("E-mail de ativação simulado enviado com sucesso.");
    }

    /**
     * Simula o envio de um e-mail de confirmação de agendamento.
     *
     * @param toEmail O endereço de e-mail do destinatário.
     * @param details Detalhes do agendamento.
     */
    public void sendAppointmentConfirmationEmail(String toEmail, String details) {
        log.info("Simulando envio de e-mail de confirmação de agendamento para: {}", toEmail);
        log.info("Detalhes do Agendamento: {}", details);
        log.info("E-mail de confirmação de agendamento simulado enviado com sucesso.");
    }

    // Outros métodos de envio de e-mail podem ser adicionados conforme a necessidade
}
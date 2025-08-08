package br.com.legalconnect.auth.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String subject;
    private String htmlBody; // Para e-mails com HTML direto
    private String templateName; // Para e-mails com template Thymeleaf
    private Map<String, Object> templateVariables; // Variáveis para o template

}
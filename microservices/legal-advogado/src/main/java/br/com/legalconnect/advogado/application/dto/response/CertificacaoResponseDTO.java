package br.com.legalconnect.advogado.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Certificação.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificacaoResponseDTO {
    private UUID id;
    private String nome;
    private String instituicao;
    private LocalDate dataConclusao;
}
package br.com.legalconnect.depoimento.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respostas de depoimentos.
 * Expõe apenas os campos relevantes para o cliente da API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepoimentoResponseDTO {
    private UUID id;
    private String texto;
    private String nome;
    private String local;
    private String fotoUrl;
    private UUID userId; // Expor o ID do usuário
    private String tipoDepoimento; // Expor o tipo como String
    private String status; // Expor o status como String
    private LocalDateTime createdAt; // Data de criação
    private LocalDateTime updatedAt; // Data de atualização
}
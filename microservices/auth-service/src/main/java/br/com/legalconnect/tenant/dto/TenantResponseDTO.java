package br.com.legalconnect.tenant.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class TenantResponseDTO
 * @brief DTO para a resposta de um Tenant.
 *
 *        Representa os dados de um Tenant que são retornados pela API,
 *        incluindo seu ID, nome, nome do esquema e status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponseDTO {
    private UUID id;
    private String nome;
    private String schemaName;
    private String status; // Representação em String do enum TenantStatus
    private Instant createdAt;
    private Instant updatedAt;
}
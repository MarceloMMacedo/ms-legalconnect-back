package br.com.legalconnect.advogado.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Role de Profissional.
 * Usado para retornar detalhes completos da Role.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleProfissionalResponseDTO {
    private UUID id;
    private String name;
    // O tenantId pode ser incluído se for relevante para o frontend
    // private UUID tenantId;
}
package br.com.legalconnect.depoimento.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de criação/atualização de depoimentos.
 * Inclui validações para garantir a integridade dos dados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepoimentoRequestDTO {

    @NotBlank(message = "O texto do depoimento é obrigatório.")
    @Size(max = 500, message = "O texto do depoimento não pode exceder 500 caracteres.")
    private String texto;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    private String nome;

    @Size(max = 100, message = "O local não pode exceder 100 caracteres.")
    private String local;

    @Size(max = 255, message = "A URL da foto não pode exceder 255 caracteres.")
    @Pattern(regexp = "^(https?|ftp)://[^\s/$.?#].[^\s]*$", message = "URL da foto inválida.")
    private String fotoUrl;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private UUID userId;

    @NotBlank(message = "O tipo de depoimento é obrigatório.")
    @Pattern(regexp = "CLIENTE|PROFISSIONAL", message = "O tipo de depoimento deve ser CLIENTE ou PROFISSIONAL.")
    private String tipoDepoimento; // Recebido como String e convertido para Enum no Mapper

    // Novo campo para o status do depoimento. Opcional na requisição para usuários
    // comuns.
    @Pattern(regexp = "PENDENTE|APROVADO|REPROVADO", message = "O status do depoimento deve ser PENDENTE, APROVADO ou REPROVADO.")
    private String status; // Recebido como String e convertido para Enum no Mapper
}
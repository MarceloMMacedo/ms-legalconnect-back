package br.com.legalconnect.advogado.application.dto.request;

import java.util.List;
import java.util.UUID;

import br.com.legalconnect.commom.dto.request.PessoaRequestDTO; // Importar PessoaRequestDTO
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para criação de um novo Profissional.
 * Este DTO agora compõe um PessoaRequestDTO para os dados de Pessoa e Usuário.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalCreateRequest extends PessoaRequestDTO {

    @NotBlank(message = "O número da OAB é obrigatório.")
    @Size(min = 5, max = 50, message = "O número da OAB deve ter entre 5 e 50 caracteres.")
    private String numeroOab;

    @NotNull(message = "A indicação de uso do marketplace é obrigatória.")
    private Boolean usaMarketplace;

    @NotNull(message = "O ID do plano é obrigatório.")
    private UUID planoId;

    @NotNull(message = "O ID do tenant é obrigatório.")
    private UUID tenantId;

    // Relacionamentos para dados mestres que podem ser definidos na criação
    private List<UUID> locaisAtuacaoIds;
    private List<UUID> areaAtuacaoIds;
    private List<UUID> idiomaIds;
    private List<UUID> tipoAtendimentoIds;

    // DTOs para certificações, experiências e formações na criação (opcional)
    @Valid
    private List<CertificacaoRequestDTO> certificacoes;
    @Valid
    private List<ExperienciaProfissionalRequestDTO> experiencias;
    @Valid
    private List<FormacaoAcademicaRequestDTO> formacoes;
}
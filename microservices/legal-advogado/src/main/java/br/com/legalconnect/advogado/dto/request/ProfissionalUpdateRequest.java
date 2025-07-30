package br.com.legalconnect.advogado.dto.request;

import java.util.List;
import java.util.UUID;

import br.com.legalconnect.commom.dto.request.PessoaRequestDTO; // Importar PessoaRequestDTO
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para atualização de um Profissional.
 * Permite a atualização dos dados da Pessoa associada e dos campos específicos
 * de Profissional,
 * bem como de listas aninhadas.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfissionalUpdateRequest {
    @NotNull(message = "O ID do profissional é obrigatório para atualização.")
    private UUID id;

    @Valid
    // A PessoaRequestDTO interna deve ter seu próprio ID se for uma atualização de
    // pessoa existente
    private PessoaRequestDTO pessoa; // Composição: Profissional 'tem uma' Pessoa

    @NotBlank(message = "O número da OAB do profissional é obrigatório para atualização.")
    private String numeroOab;

    @NotNull(message = "A indicação de uso do marketplace é obrigatória.")
    private Boolean usaMarketplace;

    private UUID empresaId; // Pode ser nulo se não houver empresa associada ou se for desvinculada

    private UUID planoId; // Pode ser nulo se o plano não for alterado

    // Listas de UUIDs para dados mestres
    private List<UUID> locaisAtuacaoIds;
    private List<UUID> areaAtuacaoIds;
    private List<UUID> idiomaIds;
    private List<UUID> tipoAtendimentoIds;

    // Listas de DTOs aninhados para certificações, experiências, formações
    // O ID em cada DTO aninhado indicará se é uma criação, atualização ou remoção.
    @Valid
    private List<CertificacaoRequestDTO> certificacoes;
    @Valid
    private List<ExperienciaProfissionalRequestDTO> experiencias;
    @Valid
    private List<FormacaoAcademicaRequestDTO> formacoes;
}
package br.com.legalconnect.advogado.application.dto.response;

import java.util.List;
import java.util.UUID;

import br.com.legalconnect.commom.dto.response.PessoaResponseDTO; // Importar PessoaResponseDTO
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de resposta detalhada para um Profissional.
 * Agora compõe um PessoaResponseDTO para os dados de Pessoa e Usuário.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfissionalResponseDTO {
    private UUID id; // ID do Profissional

    private PessoaResponseDTO pessoa; // Composição: Profissional 'tem uma' Pessoa

    private String numeroOab;
    private String statusProfissional;
    private Boolean usaMarketplace;
    private Boolean fazParteDePlano;

    // IDs de entidades relacionadas
    private UUID empresaId;
    private UUID planoId;
    private UUID tenantId;

    // Listas de DTOs aninhados
    private List<CertificacaoResponseDTO> certificacoes;
    private List<DocumentoResponseDTO> documentos;
    private List<ExperienciaProfissionalResponseDTO> experiencias;
    private List<FormacaoAcademicaResponseDTO> formacoes;

    // Listas de DTOs de dados mestres (detalhados)
    private List<LocalAtuacaoResponseDTO> locaisAtuacao;
    private List<AreaAtuacaoResponseDTO> areasAtuacao;
    private List<IdiomaResponseDTO> idiomas;
    private List<TipoAtendimentoResponseDTO> tiposAtendimento;
    private List<RoleProfissionalResponseDTO> rolesProfissional; // DTO para Role do Profissional
}
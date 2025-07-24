package br.com.legalconnect.advogado.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfissionalDetalhadoResponse {
    private UUID id;
    private String numeroOab;
    private String statusProfissional;
    private Boolean usaMarketplace;
    private Boolean fazParteDePlano;
    private UUID pessoaId;
    private UUID empresaId;
    private UUID planoId;
    private UUID tenantId;

    private String nomeCompletoPessoa;
    private String cpfPessoa;
    private LocalDate dataNascimentoPessoa;
    private List<String> telefonesPessoa;

    private List<CertificacaoResponseDTO> certificacoes;
    private List<DocumentoResponseDTO> documentos;
    private List<ExperienciaProfissionalResponseDTO> experiencias;
    private List<FormacaoAcademicaResponseDTO> formacoes;

    private List<UUID> locaisAtuacaoIds;
    private List<UUID> areaAtuacaoIds;
    private List<UUID> idiomaIds;
    private List<UUID> tipoAtendimentoIds;
    private List<UUID> roleProfissionalIds;
}
package br.com.legalconnect.advogado.application.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
public class AtualizacaoPerfilRequest {
    @NotBlank(message = "O número da OAB do profissional é obrigatório para atualização.")
    private String numeroOab;

    @NotNull(message = "A indicação de uso do marketplace é obrigatória.")
    private Boolean usaMarketplace;

    private UUID empresaId;

    private UUID planoId;

    @Size(max = 255, message = "O nome completo da pessoa deve ter no máximo 255 caracteres.")
    private String nomeCompletoPessoa;

    @PastOrPresent(message = "A data de nascimento da pessoa não pode ser uma data futura.")
    private LocalDate dataNascimentoPessoa;

    @Size(max = 10, message = "A lista de telefones não pode exceder 10 itens.")
    private List<@NotBlank(message = "O número de telefone não pode ser vazio.") @Size(max = 20, message = "O número de telefone deve ter no máximo 20 caracteres.") String> telefonesPessoa;

    private List<@NotNull(message = "O ID do local de atuação não pode ser nulo.") UUID> locaisAtuacaoIds;

    private List<@NotNull(message = "O ID da área de atuação não pode ser nulo.") UUID> areaAtuacaoIds;

    private List<@NotNull(message = "O ID do idioma não pode ser nulo.") UUID> idiomaIds;

    private List<@NotNull(message = "O ID do tipo de atendimento não pode ser nulo.") UUID> tipoAtendimentoIds;

    @Valid
    private List<CertificacaoRequestDTO> certificacoes;

    @Valid
    private List<ExperienciaProfissionalRequestDTO> experiencias;

    @Valid
    private List<FormacaoAcademicaRequestDTO> formacoes;
}
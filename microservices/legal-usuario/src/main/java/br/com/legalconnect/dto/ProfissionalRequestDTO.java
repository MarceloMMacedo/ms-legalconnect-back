package br.com.legalconnect.dto;

import java.util.UUID;

import br.com.legalconnect.entity.Profissional.StatusProfissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class ProfissionalRequestDTO
 * @brief DTO para requisições de criação ou atualização de Profissional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProfissionalRequestDTO extends PessoaRequestDTO {
    @NotBlank(message = "O número da OAB não pode estar em branco.")
    @Size(max = 50, message = "O número da OAB deve ter no máximo 50 caracteres.")
    private String numeroOab;

    @NotNull(message = "O status profissional não pode ser nulo.")
    private StatusProfissional statusProfissional;

    @NotNull(message = "A indicação de uso do marketplace não pode ser nula.")
    private Boolean usaMarketplace;

    @NotNull(message = "A indicação de participação em plano não pode ser nula.")
    private Boolean fazParteDePlano;

    private UUID empresaId; // ID da empresa associada (opcional)

    @NotNull(message = "O ID do plano não pode ser nulo.")
    private UUID planoId; // ID do plano de assinatura
}
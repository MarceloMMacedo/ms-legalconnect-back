package br.com.legalconnect.advogado.application.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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
public class ProfissionalCreateRequest {
    @NotBlank(message = "O número da OAB é obrigatório.")
    @Size(min = 5, max = 50, message = "O número da OAB deve ter entre 5 e 50 caracteres.")
    private String numeroOab;

    @NotBlank(message = "O e-mail do usuário é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Size(max = 255, message = "O e-mail do usuário deve ter no máximo 255 caracteres.")
    private String emailUsuario;

    @NotBlank(message = "A senha do usuário é obrigatória.")
    @Size(min = 8, max = 255, message = "A senha do usuário deve ter no mínimo 8 caracteres.")
    private String senhaUsuario;

    @NotBlank(message = "O nome completo da pessoa é obrigatório.")
    @Size(max = 255, message = "O nome completo da pessoa deve ter no máximo 255 caracteres.")
    private String nomeCompletoPessoa;

    @NotBlank(message = "O CPF da pessoa é obrigatório.")
    @Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres (com ou sem formatação).")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$|^\\d{11}$", message = "Formato de CPF inválido. Use '000.000.000-00' ou apenas dígitos.")
    private String cpfPessoa;

    @NotNull(message = "A data de nascimento da pessoa é obrigatória.")
    @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
    private LocalDate dataNascimentoPessoa;

    @NotNull(message = "O ID do plano é obrigatório.")
    private UUID planoId;

    @NotNull(message = "O ID do tenant é obrigatório.")
    private UUID tenantId;
}
package br.com.legalconnect.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class UserStatusUpdateRequest
 * @brief DTO (Data Transfer Object) para a requisição de atualização de status
 *        de usuário.
 *
 *        Esta classe é utilizada para encapsular o novo status que será enviado
 *        na requisição para alterar o estado de um usuário no sistema.
 */
@Data // Adiciona getters, setters, toString, equals e hashCode
@NoArgsConstructor // Adiciona construtor sem argumentos
@AllArgsConstructor // Adiciona construtor com todos os argumentos
public class UserStatusUpdateRequest {
    /**
     * @brief O novo status do usuário.
     *        Deve ser uma string que corresponde a um dos valores do enum
     *        User.UserStatus
     *        (ex: "ACTIVE", "INACTIVE", "PENDING_APPROVAL", "REJECTED", "PENDING").
     *
     * @NotBlank Garante que o campo não seja nulo e não contenha apenas espaços em
     *           branco.
     */
    @NotBlank(message = "O novo status é obrigatório.")
    private String newStatus;
}

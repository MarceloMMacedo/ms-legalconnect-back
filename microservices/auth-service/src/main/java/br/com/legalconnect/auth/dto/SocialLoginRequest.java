package br.com.legalconnect.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;

/**
 * @class SocialLoginRequest
 * @brief DTO para requisição de login social (token do provedor).
 *
 * Utilizado para autenticação via provedores de identidade externos como Google ou LinkedIn.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginRequest {
    @NotBlank(message = "O provedor social é obrigatório.")
    private String provider; // Ex: "GOOGLE", "LINKEDIN"

    @NotBlank(message = "O token do provedor é obrigatório.")
    private String token; // O token OAuth real
}
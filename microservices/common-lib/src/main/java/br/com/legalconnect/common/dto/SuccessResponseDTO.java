package br.com.legalconnect.common.dto;

import java.time.LocalDateTime;

import br.com.legalconnect.enums.StatusResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @class SuccessResponseDTO
 * @brief DTO para respostas de sucesso simples da API.
 *
 *        Estende {@link BaseResponse} e é usado para indicar operações
 *        bem-sucedidas
 *        que podem ou não retornar dados específicos.
 */
@Data
@EqualsAndHashCode(callSuper = true) // Inclui campos da superclasse no equals e hashCode
public class SuccessResponseDTO extends BaseResponse {

    /**
     * Construtor padrão para uma resposta de sucesso.
     * Define o status como StatusResponse.SUCESSO e o timestamp atual.
     */
    public SuccessResponseDTO() {
        this.setStatus(StatusResponse.SUCESSO);
        this.setTimestamp(LocalDateTime.now());
    }

    /**
     * Construtor para uma resposta de sucesso com uma mensagem específica.
     *
     * @param message A mensagem descritiva do sucesso.
     */
    public SuccessResponseDTO(String message) {
        this(); // Chama o construtor padrão para definir status e timestamp
        this.setMessage(message);
    }
}
package br.com.legalconnect.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import br.com.legalconnect.enums.StatusResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class BaseResponse
 * @brief DTO base para padronizar as respostas da API.
 *
 *        Inclui campos comuns para todas as respostas, como status, mensagem,
 *        timestamp e, opcionalmente, uma lista de erros.
 *        Utiliza Lombok para reduzir boilerplate (getters, setters,
 *        construtores, builder).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Inclui apenas campos não nulos na serialização JSON
public class BaseResponse<T> {

    private StatusResponse status; // Status da resposta (ex: StatusResponse.SUCESSO, StatusResponse.ERRO,
                                   // "WARNING")
    private String message; // Mensagem descritiva da resposta
    private LocalDateTime timestamp; // Data e hora da resposta
    private T data; // Payload da resposta (dados de sucesso)
    private List<String> errors; // Lista de mensagens de erro, se houver
}

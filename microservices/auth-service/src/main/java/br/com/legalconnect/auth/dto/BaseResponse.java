package br.com.legalconnect.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @class BaseResponse
 * @brief DTO genérico para padronizar as respostas da API.
 * @param <T> Tipo do payload de dados da resposta.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Inclui apenas campos não nulos na serialização JSON
public class BaseResponse<T> {
    private String status; // Status da resposta (ex: "SUCCESS", "ERROR", "WARNING")
    private String message; // Mensagem descritiva da resposta
    private LocalDateTime timestamp; // Data e hora da resposta
    private List<String> errors; // Lista de mensagens de erro, se houver
    private T data; // Payload da resposta (dados de sucesso)
}
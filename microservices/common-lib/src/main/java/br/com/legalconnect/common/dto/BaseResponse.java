package br.com.legalconnect.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @class BaseResponse
 * @brief DTO base para padronizar as respostas da API.
 *
 * Inclui campos comuns para todas as respostas, como status, mensagem,
 * timestamp e, opcionalmente, uma lista de erros.
 * Utiliza Lombok para reduzir boilerplate (getters, setters, construtores, builder).
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Inclui apenas campos não nulos na serialização JSON
public class BaseResponse {

    private String status; // Status da resposta (ex: "SUCCESS", "ERROR", "WARNING")
    private String message; // Mensagem descritiva da resposta
    private LocalDateTime timestamp; // Data e hora da resposta
    private List<String> errors; // Lista de mensagens de erro, se houver
}
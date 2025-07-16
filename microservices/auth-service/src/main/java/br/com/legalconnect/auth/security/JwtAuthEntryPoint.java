package br.com.legalconnect.auth.security;

import java.io.IOException;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.legalconnect.common.common_lib.BaseResponse;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class JwtAuthEntryPoint
 * @brief Ponto de entrada de autenticação para requisições não autenticadas ou
 *        com falha de autenticação.
 *
 *        Esta classe implementa `AuthenticationEntryPoint` para lidar com
 *        requisições que
 *        chegam sem credenciais válidas ou com credenciais que falham na
 *        autenticação.
 *        Ela retorna uma resposta JSON padronizada com `HTTP 401 Unauthorized`
 *        e um
 *        `BaseResponse` contendo o código e mensagem de erro.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class); // Instância do Logger

    @Autowired
    private ObjectMapper objectMapper; // Para converter o objeto de resposta em JSON

    /**
     * @brief Lida com requisições não autenticadas ou com falha de autenticação.
     *
     *        Retorna uma resposta HTTP 401 Unauthorized com um corpo JSON
     *        padronizado,
     *        utilizando o `BaseResponse` e o `ErrorCode.UNAUTHORIZED_ACCESS`.
     *
     * @param request       A requisição HTTP.
     * @param response      A resposta HTTP.
     * @param authException A exceção de autenticação que causou a falha.
     * @throws IOException      Se ocorrer um erro de I/O ao escrever a resposta.
     * @throws ServletException Se ocorrer um erro de servlet.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.error("Erro de autenticação: {}", authException.getMessage(), authException); // Loga o erro de autenticação

        // Define o status HTTP como 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Define o tipo de conteúdo da resposta como JSON
        response.setContentType("application/json");

        // Constrói o objeto de resposta padronizado
        BaseResponse<Void> errorResponse = BaseResponse.<Void>builder()
                .codigoErro(ErrorCode.UNAUTHORIZED_ACCESS.getCode())
                .mensagemErro(ErrorCode.UNAUTHORIZED_ACCESS.getMessage() + ": " + authException.getMessage())
                .build();

        // Escreve o objeto JSON na resposta
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
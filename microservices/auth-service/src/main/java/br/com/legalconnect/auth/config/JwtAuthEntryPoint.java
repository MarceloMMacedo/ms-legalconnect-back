package br.com.legalconnect.auth.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
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
public class JwtAuthEntryPoint extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Validação do Tenant ID
            String tenantId = request.getHeader(TENANT_HEADER);
            if (tenantId == null || tenantId.isBlank()) {
                throw new BusinessException(ErrorCode.TENANT_NOT_FOUND, TENANT_HEADER + " header is required");
            }

            // 2. Validação do Correlation ID
            String correlationId = request.getHeader(CORRELATION_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND, CORRELATION_HEADER + " header is required");
            }

            // 3. Se tudo válido, prossegue com a requisição
            filterChain.doFilter(request, response);

        } catch (MissingHeaderException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    // Exceção customizada para headers faltantes
    private static class MissingHeaderException extends RuntimeException {
        public MissingHeaderException(String message) {
            super(message);
        }
    }
}
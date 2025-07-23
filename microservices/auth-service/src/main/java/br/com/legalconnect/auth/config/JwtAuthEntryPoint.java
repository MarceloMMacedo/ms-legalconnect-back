package br.com.legalconnect.auth.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import reactor.core.publisher.Mono;

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
public class JwtAuthEntryPoint implements WebFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.fromRunnable(() -> {
            // 1. Validação do Tenant ID
            String tenantId = exchange.getRequest().getHeaders().getFirst(TENANT_HEADER);
            if (tenantId == null || tenantId.isBlank()) {
                throw new BusinessException(ErrorCode.TENANT_NOT_FOUND, TENANT_HEADER + " header is required");
            }

            // 2. Validação do Correlation ID
            String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND, CORRELATION_HEADER + " header is required");
            }
        }).then(chain.filter(exchange));
    }
}
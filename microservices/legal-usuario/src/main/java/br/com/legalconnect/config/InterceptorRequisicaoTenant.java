package main.java.br.com.legalconnect.config;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.legalconnect.comum.multitenant.ContextoTenant; // Assumindo que esta classe está na common-lib

@Slf4j
@Component
public class InterceptorRequisicaoTenant extends OncePerRequestFilter {

    @Value("${application.tenant.header-name:X-Tenant-ID}")
    private String tenantHeaderName;

    @Value("${application.user.header-name:X-User-ID}")
    private String userHeaderName;

    @Value("${application.tenant.default-id:public}")
    private String defaultTenantId;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Extrair Cabeçalhos
            String tenantId = Optional.ofNullable(request.getHeader(tenantHeaderName))
                    .orElse(defaultTenantId); // Regra de fallback para tenantId
            String userId = request.getHeader(userHeaderName); // userId é opcional

            // 2. Popular ContextoTenant
            ContextoTenant.setTenantId(tenantId);
            ContextoTenant.setUserId(userId); // Pode ser null se não estiver presente

            logger.debug("Requisição para URI: {}, Tenant ID: {}, User ID: {}", request.getRequestURI(), tenantId,
                    userId);

            // 3. Validação (opcional, dependendo da sua regra de negócio)
            // Exemplo: se uma rota específica *sempre* exigir um tenantId explícito no
            // header
            /*
             * if (request.getRequestURI().startsWith("/api/v1/empresas") &&
             * tenantId.equals(defaultTenantId)) {
             * logger.
             * warn("Acesso negado: Requisição para rota de empresa sem X-Tenant-ID explícito. URI: {}"
             * , request.getRequestURI());
             * response.sendError(HttpServletResponse.SC_BAD_REQUEST,
             * "X-Tenant-ID é obrigatório para esta operação.");
             * return;
             * }
             */

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Erro no InterceptorRequisicaoTenant para URI: {}. Erro: {}", request.getRequestURI(),
                    e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
        } finally {
            // 4. Limpeza: Garantir que o ContextoTenant seja limpo ao final da requisição
            ContextoTenant.clear();
            logger.debug("ContextoTenant limpo para URI: {}", request.getRequestURI());
        }
    }
}

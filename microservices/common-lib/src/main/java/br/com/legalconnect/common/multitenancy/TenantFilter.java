package br.com.legalconnect.common.multitenancy;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @class TenantFilter
 * @brief Filtro HTTP para extrair o identificador do tenant de cada requisição.
 *
 *        Este filtro é executado uma vez por requisição e tenta obter o tenant
 *        ID
 *        de um cabeçalho HTTP (configurável via propriedade). Se encontrado, o
 *        tenant ID
 *        é definido no {@link TenantContext} para ser usado por outras partes
 *        da aplicação
 *        (ex: Hibernate). Garante que o contexto seja limpo após a requisição.
 */
@Component
@Order(1) // Garante que este filtro seja executado antes de outros filtros de segurança
          // ou de negócio.
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    @Value("${application.tenant.header-name:X-Tenant-ID}")
    private String tenantHeaderName; // Nome do cabeçalho HTTP para o tenant ID

    @Value("${application.tenant.default-id}")
    private String defaultTenantId; // O ID do tenant padrão

    /**
     * @brief Realiza a filtragem para cada requisição HTTP.
     *
     *        Extrai o tenant ID do cabeçalho da requisição. Se não for encontrado,
     *        usa o tenant ID padrão. Define o tenant ID no {@link TenantContext}
     *        antes de prosseguir com a cadeia de filtros e o limpa no final.
     *
     * @param request     O objeto HttpServletRequest.
     * @param response    O objeto HttpServletResponse.
     * @param filterChain A cadeia de filtros.
     * @throws jakarta.servlet.ServletException Se ocorrer um erro de servlet.
     * @throws IOException                      Se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {

        String tenantId = request.getHeader(tenantHeaderName);

        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = defaultTenantId; // Usa o tenant padrão se o cabeçalho não for fornecido
            log.debug("TenantFilter: Cabeçalho '{}' não encontrado ou vazio. Usando tenant padrão: {}",
                    tenantHeaderName, defaultTenantId);
        } else {
            log.debug("TenantFilter: Tenant ID '{}' encontrado no cabeçalho '{}'.", tenantId, tenantHeaderName);
        }

        TenantContext.setTenantId(tenantId); // Define o tenant ID no contexto da thread

        try {
            filterChain.doFilter(request, response); // Continua com a cadeia de filtros
        } finally {
            TenantContext.clear(); // Limpa o tenant ID do contexto da thread para evitar vazamentos
            log.debug("TenantFilter: Tenant ID limpo do contexto da thread.");
        }
    }
}
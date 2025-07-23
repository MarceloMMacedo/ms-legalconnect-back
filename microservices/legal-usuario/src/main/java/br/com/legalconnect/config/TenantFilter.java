package br.com.legalconnect.config; // Importa da common-lib

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.legalconnect.common.config.TenantMigrationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class TenantFilter
 * @brief Filtro HTTP para interceptar requisições e extrair o ID do tenant.
 *        Define o tenant ID no TenantContext para que o Hibernate possa usá-lo.
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    @Value("${application.tenant.default-id}")
    private String defaultTenantId;

    @Autowired
    private TenantMigrationService tenantMigrationService;

    private static final String TENANT_HEADER = "X-Tenant-ID";

    /**
     * @brief Filtra a requisição para extrair e definir o ID do tenant.
     * @param request     A requisição HTTP.
     * @param response    A resposta HTTP.
     * @param filterChain A cadeia de filtros.
     * @throws ServletException Se ocorrer um erro de servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String tenantIdHeader = request.getHeader(TENANT_HEADER);
        String tenantId = "public";

        if (tenantIdHeader != null && !tenantIdHeader.isEmpty()) {
            try {
                tenantId = (tenantIdHeader);
            } catch (IllegalArgumentException e) {
                // Logar ou tratar o erro de UUID inválido, talvez retornar um 400 Bad Request
                System.err.println("UUID de Tenant inválido no cabeçalho X-Tenant-ID: " + tenantIdHeader);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "UUID de Tenant inválido.");
                return;
            }
        } else {
            // Usar o tenant padrão se o cabeçalho não for fornecido
            try {
                tenantId = (defaultTenantId);
            } catch (IllegalArgumentException e) {
                System.err.println("UUID de Tenant padrão inválido na configuração: " + defaultTenantId);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Configuração de Tenant padrão inválida.");
                return;
            }
        }

        tenantMigrationService.migrateTenant(tenantId);
        filterChain.doFilter(request, response);

    }
}
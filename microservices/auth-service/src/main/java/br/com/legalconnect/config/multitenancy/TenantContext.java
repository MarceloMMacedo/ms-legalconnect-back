package br.com.legalconnect.config.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @class TenantContext
 * @brief Contexto de Tenant usando ThreadLocal para armazenar o ID do tenant atual.
 * Permite que o ID do tenant seja acessado em qualquer parte da aplicação
 * dentro do mesmo thread de execução.
 * Adiciona o tenantId ao MDC para logging contextual.
 */
public class TenantContext {

    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private static final String MDC_TENANT_ID_KEY = "tenantId";

    /**
     * Define o ID do tenant atual para o thread e o adiciona ao MDC.
     * @param tenantId O ID do tenant a ser definido.
     */
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
        MDC.put(MDC_TENANT_ID_KEY, tenantId);
        log.trace("Tenant ID '{}' definido no contexto e MDC.", tenantId);
    }

    /**
     * Obtém o ID do tenant atual do thread.
     * @return O ID do tenant atual.
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Limpa o ID do tenant do thread e remove do MDC.
     * Deve ser chamado ao final de cada requisição para evitar vazamentos de contexto.
     */
    public static void clear() {
        currentTenant.remove();
        MDC.remove(MDC_TENANT_ID_KEY);
        log.trace("Tenant ID removido do contexto e MDC.");
    }
}
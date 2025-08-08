package br.com.legalconnect.commom.service;

import java.util.UUID;

/**
 * Utilitário para gerenciar o ID do Tenant no contexto da thread atual.
 * Usado em arquiteturas multitenant para garantir que as operações de banco de dados
 * sejam filtradas pelo tenant correto.
 */
public class TenantContext {

    private static final ThreadLocal<UUID> currentTenant = new ThreadLocal<>();

    /**
     * Define o ID do Tenant para a thread atual.
     *
     * @param tenantId O ID do Tenant.
     */
    public static void setCurrentTenantId(UUID tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Retorna o ID do Tenant da thread atual.
     *
     * @return O ID do Tenant.
     * @throws IllegalStateException se o Tenant ID não estiver definido no contexto.
     */
    public static UUID getCurrentTenantId() {
        UUID tenantId = currentTenant.get();
        if (tenantId == null) {
            // Em um ambiente real, você pode lançar uma exceção ou retornar um tenant padrão
            // dependendo da lógica de segurança e acesso.
            // Por simplicidade para este exercício, lançaremos uma exceção.
            throw new IllegalStateException("Tenant ID não está definido no contexto da requisição. Verifique o filtro de segurança.");
        }
        return tenantId;
    }

    /**
     * Limpa o ID do Tenant da thread atual.
     * Deve ser chamado ao final da requisição para evitar vazamentos de contexto.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
package br.com.legalconnect.common.config.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @class CurrentTenantIdentifierResolverImpl
 * @brief Implementação de CurrentTenantIdentifierResolver do Hibernate.
 *        Informa ao Hibernate qual é o tenant atual para rotear as operações de
 *        banco de dados.
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final Logger log = LoggerFactory.getLogger(CurrentTenantIdentifierResolverImpl.class);
    private static final String DEFAULT_TENANT_ID = "public"; // Schema padrão para tabelas globais (ex: tb_tenant)

    /**
     * Resolve o identificador do tenant atual.
     * Se um tenant estiver definido no TenantContext, ele é retornado.
     * Caso contrário, o tenant padrão "public" é retornado.
     * 
     * @return O identificador do tenant atual.
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            log.debug("Nenhum tenant definido no contexto, usando tenant padrão: {}", DEFAULT_TENANT_ID);
            return DEFAULT_TENANT_ID;
        }
        log.trace("Resolvendo tenant atual: {}", tenantId);
        return tenantId;
    }

    /**
     * Indica se o identificador do tenant é validado.
     * 
     * @return True se a validação é necessária, false caso contrário.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
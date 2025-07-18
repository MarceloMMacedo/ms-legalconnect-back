package br.com.legalconnect.bffpessoas.config;

import br.com.legalconnect.common.config.multitenancy.TenantContext; // Importa TenantContext da common-lib
import br.com.legalconnect.user.entity.User; // Importa a entidade User da common-lib ou user-service
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @class FeignClientConfig
 * @brief Configuração global para os FeignClients no BFF.
 *
 * Esta classe define um interceptor de requisição que será responsável por
 * injetar os cabeçalhos `X-User-ID` e `X-Tenant-ID` em cada requisição
 * feita por um FeignClient para os microsserviços de backend.
 */
@Configuration
public class FeignClientConfig {

    private static final Logger log = LoggerFactory.getLogger(FeignClientConfig.class);

    /**
     * @brief Cria um bean `RequestInterceptor` para injetar cabeçalhos em requisições Feign.
     *
     * Este interceptor obtém o `userId` do `SecurityContextHolder` (populado pelo
     * `JwtTokenFilter`) e o `tenantId` do `TenantContext` (também populado pelo filtro).
     * Esses IDs são então adicionados como cabeçalhos `X-User-ID` e `X-Tenant-ID`
     * para serem propagados aos microsserviços de downstream.
     * @return Uma instância de `RequestInterceptor`.
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        log.info("Configurando Feign Request Interceptor para injeção de X-User-ID e X-Tenant-ID.");
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 1. Tenta obter o userId do SecurityContextHolder
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof User) {
                    // Assumimos que o principal é uma instância da entidade User
                    // Se você estiver usando CustomUserDetails (como no auth-service), ajuste o cast.
                    User user = (User) authentication.getPrincipal();
                    if (user.getId() != null) {
                        template.header("X-User-ID", user.getId().toString());
                        log.debug("FeignInterceptor: X-User-ID adicionado: {}", user.getId());
                    } else {
                        log.warn("FeignInterceptor: Principal (User) sem ID. Não adicionando X-User-ID.");
                    }
                } else {
                    log.debug("FeignInterceptor: Nenhuma autenticação ou principal não é do tipo User. Não adicionando X-User-ID.");
                }

                // 2. Tenta obter o tenantId do TenantContext (populado pelo JwtTokenFilter)
                String tenantId = TenantContext.getCurrentTenant();
                if (tenantId != null) {
                    template.header("X-Tenant-ID", tenantId);
                    log.debug("FeignInterceptor: X-Tenant-ID adicionado: {}", tenantId);
                } else {
                    log.warn("FeignInterceptor: TenantContext sem tenantId. Não adicionando X-Tenant-ID.");
                }
            }
        };
    }
}
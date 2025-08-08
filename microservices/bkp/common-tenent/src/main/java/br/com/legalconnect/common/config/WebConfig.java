package br.com.legalconnect.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import br.com.legalconnect.common.config.multitenancy.TenantInterceptor;
import lombok.RequiredArgsConstructor;

/**
 * @class WebConfig
 * @brief Configuração web para registrar interceptores.
 *        Adiciona o TenantInterceptor para gerenciar o contexto do tenant em
 *        cada requisição.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    private final TenantInterceptor tenantInterceptor;

    /**
     * Adiciona interceptores à lista de interceptores do Spring MVC.
     * 
     * @param registry O registro de interceptores.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.debug("Registrando TenantInterceptor.");
        registry.addInterceptor(tenantInterceptor);
    }
}
package br.com.legalconnect.common.config.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @class TenantInterceptor
 * @brief Interceptor de requisições para definir o ID do tenant no
 *        TenantContext.
 *        Extrai o ID do tenant do cabeçalho da requisição (ex: "X-Tenant-ID").
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);
    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Value("${application.tenant.default-id}") // Injeção da propriedade defaultTenantId
    private String defaultTenantId;

    /**
     * Pré-processamento da requisição: extrai o ID do tenant do cabeçalho
     * e o define no TenantContext.
     * 
     * @param request  A requisição HTTP.
     * @param response A resposta HTTP.
     * @param handler  O handler da requisição.
     * @return True para continuar o processamento, false para interromper.
     * @throws Exception Se ocorrer um erro.
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setCurrentTenant(defaultTenantId);
            log.debug("Tenant ID '{}' extraído do cabeçalho da requisição.", tenantId);
        } else {
            // Se o cabeçalho não for fornecido, usa o defaultTenantId configurado
            TenantContext.setCurrentTenant(defaultTenantId);
            log.warn("Cabeçalho '{}' não encontrado ou vazio. Usando tenant padrão '{}'.", TENANT_HEADER,
                    defaultTenantId);
        }
        return true;
    }

    /**
     * Pós-processamento da requisição: limpa o TenantContext.
     * 
     * @param request      A requisição HTTP.
     * @param response     A resposta HTTP.
     * @param handler      O handler da requisição.
     * @param modelAndView O ModelAndView gerado pelo handler.
     * @throws Exception Se ocorrer um erro.
     */
    @Override
    public void postHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            ModelAndView modelAndView) throws Exception {
        // Não é estritamente necessário limpar aqui se o afterCompletion for sempre
        // chamado,
        // mas pode ser útil para cenários específicos.
    }

    /**
     * Conclusão da requisição: garante que o TenantContext seja limpo,
     * independentemente do resultado da requisição (sucesso ou erro).
     * 
     * @param request  A requisição HTTP.
     * @param response A resposta HTTP.
     * @param handler  O handler da requisição.
     * @param ex       A exceção que ocorreu durante o processamento (se houver).
     * @throws Exception Se ocorrer um erro.
     */
    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) throws Exception {
        TenantContext.clear(); // Garante que o ThreadLocal e MDC sejam limpos após a requisição
        log.debug("TenantContext limpo após a requisição.");
    }
}
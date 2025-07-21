package br.com.legalconnect.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterAdvice implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        String userIdHeader = request.getHeader("X-Correlaton-ID");

        if (userIdHeader == null || userIdHeader.isEmpty()) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "Acesso inválido.");
        }

        return true; // retorne false para interromper a execução
    }
}

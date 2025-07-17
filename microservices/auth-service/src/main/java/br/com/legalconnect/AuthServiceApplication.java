package br.com.legalconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @class LegalconnectAuthServiceApplication
 * @brief Classe principal da aplicação Spring Boot para o microsserviço de
 *        autenticação.
 *
 *        Esta classe inicializa a aplicação, configura o escaneamento de
 *        pacotes para
 *        entidades JPA e repositórios, e define a localização dos componentes
 *        Spring
 *        para injeção de dependência, focando apenas nas classes de
 *        autenticação.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"br.com.legalconnect.auth.repository",
		"br.com.legalconnect.user.repository",
		"br.com.legalconnect.tenant.repository" // Manter se Tenant for uma entidade global acessada por auth
})
@EntityScan(basePackages = {
		"br.com.legalconnect.auth.entity",
		"br.com.legalconnect.user.entity",
		"br.com.legalconnect.common.entity" // Se BaseEntity ou AuditLog ainda forem usados
})
@ComponentScan(basePackages = {
		"br.com.legalconnect.auth",
		"br.com.legalconnect.auth.security",
		"br.com.legalconnect.auth.controller",
		"br.com.legalconnect.auth.service",
		"br.com.legalconnect.multitenancy",
		"br.com.legalconnect.user.service", // Para UserDetailsServiceImpl
		"br.com.legalconnect.user.entity", // Para User entity
		"br.com.legalconnect.user.mapper", // Para UserMapper (se usado em AuthService)
		"br.com.legalconnect.common.constants", // Para ErrorCode, Roles
		"br.com.legalconnect.common.exception", // Para BusinessException, GlobalExceptionHandler
		"br.com.legalconnect.common.service", // Para AuditLogService, NotificationService (mocks)
		"br.com.legalconnect.common.util", // Para ValidatorUtil
		"br.com.legalconnect.tenant.service", // Para TenantService (se necessário para social login ou criação de //
												// usuário)
		"br.com.legalconnect.config.security" // Configuração de segurança
})
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}

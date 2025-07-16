package br.com.legalconnect.tenant.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// Removidas as importações de java.sql.Connection, java.sql.Statement, javax.sql.DataSource, org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.auth.mapper.TenantMapper;
import br.com.legalconnect.common.constants.ErrorCode;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.service.AuditLogService;
import br.com.legalconnect.tenant.dto.TenantCreationRequest;
import br.com.legalconnect.tenant.dto.TenantResponseDTO;
import br.com.legalconnect.tenant.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @class TenantService
 * @brief Serviço responsável pelo gerenciamento de Tenants.
 *
 * Gerencia a criação, ativação e desativação de tenants, que são
 * ambientes
 * isolados para diferentes escritórios ou grupos de usuários.
 *
 * ATENÇÃO: As funcionalidades de migração de banco de dados (Flyway) foram
 * removidas deste serviço, conforme solicitado. A criação e migração de schemas
 * para novos tenants devem ser gerenciadas por um serviço externo ou ferramenta
 * de orquestração em um ambiente de produção.
 */
@Service
@Slf4j // Anotação para logging
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository; /// < Repositório para tenants.
    @Autowired
    private AuditLogService auditLogService; /// < Serviço para logs de auditoria de erros.
    @Autowired
    private TenantMapper tenantMapper; /// < Mapper para tenants.
    // @Autowired // Removido: DataSource não é mais necessário aqui após a remoção do Flyway
    // private DataSource dataSource;

    /**
     * @brief Cria um novo tenant.
     *
     * @param request DTO com os detalhes do tenant a ser criado.
     * @return O DTO do tenant criado.
     * @throws BusinessException se o nome do esquema já estiver em uso.
     */
    @Transactional
    public TenantResponseDTO createTenant(TenantCreationRequest request) {
        log.info("Iniciando criação de novo tenant com schemaName: {}", request.getSchemaName());
        if (tenantRepository.findBySchemaName(request.getSchemaName()).isPresent()) {
            log.warn("Falha na criação do tenant: Nome do esquema '{}' já em uso.", request.getSchemaName());
            auditLogService.logError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), "Criação de tenant falhou: Schema já em uso.", request.getSchemaName(), null, null);
            throw new BusinessException(ErrorCode.INVALID_REQUEST_PARAMETER, "Nome do esquema já em uso.");
        }

        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setStatus(Tenant.TenantStatus.PENDING_ACTIVATION); // Novo tenant começa como pendente de ativação
        tenant = tenantRepository.save(tenant);
        log.info("Tenant '{}' salvo no banco de dados global com ID: {}. Status: PENDING_ACTIVATION.", tenant.getNome(), tenant.getId());

        // A criação e migração do schema para o novo tenant NÃO são mais responsabilidade
        // deste serviço. Em um ambiente de produção, isso seria orquestrado por um
        // serviço de provisionamento de tenants ou uma ferramenta externa.
        log.info("Criação de tenant concluída. A migração do schema '{}' deve ser orquestrada externamente.", tenant.getSchemaName());

        // TODO: [PRODUÇÃO] Enviar e-mail de ativação com um token para o administrador do novo tenant
        // notificationService.sendTenantActivationEmail(tenant.getAdminEmail(), tenant.getId(), activationToken);
        return tenantMapper.toDto(tenant);
    }

    /**
     * @brief Ativa um tenant usando um token de ativação.
     *
     * @param token O token de ativação.
     * @return O DTO do tenant ativado.
     * @throws BusinessException se o token for inválido ou o tenant não for
     * encontrado/já ativo.
     */
    @Transactional
    public TenantResponseDTO activateTenant(String token) {
        log.info("Tentativa de ativação de tenant com token.");
        // Em um cenário real, o token seria validado (ex: JWT com ID do tenant e expiração)
        // Por simplicidade, vamos usar um mock de token.
        UUID tenantId = parseTenantIdFromActivationToken(token); // Método fictício para extrair ID do token

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("Falha na ativação do tenant: Token de ativação inválido ou expirado. Tenant ID: {}", tenantId);
                    auditLogService.logError(ErrorCode.TENANT_NOT_FOUND.getCode(), "Ativação de tenant falhou: Token inválido/expirado.", token, null, tenantId);
                    return new BusinessException(ErrorCode.TENANT_NOT_FOUND,
                        "Token de ativação inválido ou expirado.");
                });
        if (tenant.getStatus() == Tenant.TenantStatus.ACTIVE) {
            log.warn("Falha na ativação do tenant: Tenant '{}' já está ativo.", tenant.getNome());
            auditLogService.logError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), "Ativação de tenant falhou: Tenant já ativo.", tenant.getId().toString(), tenant.getId(), tenant.getId());
            throw new BusinessException(ErrorCode.INVALID_REQUEST_PARAMETER, "Tenant já está ativo.");
        }

        tenant.setStatus(Tenant.TenantStatus.ACTIVE);
        tenantRepository.save(tenant);

        log.info("Tenant '{}' (ID: {}) ativado com sucesso. Presume-se que o schema '{}' já foi migrado ou será no primeiro acesso.",
                tenant.getNome(), tenant.getId(), tenant.getSchemaName());

        return tenantMapper.toDto(tenant);
    }

    /**
     * @brief Desativa um tenant.
     *
     * @param tenantId O ID do tenant a ser desativado.
     * @throws BusinessException se o tenant não for encontrado ou já estiver
     * inativo.
     */
    @Transactional
    public void deactivateTenant(UUID tenantId) {
        log.info("Tentativa de desativação do tenant com ID: {}", tenantId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("Falha na desativação do tenant: Tenant não encontrado com ID: {}", tenantId);
                    auditLogService.logError(ErrorCode.TENANT_NOT_FOUND.getCode(), "Desativação de tenant falhou: Tenant não encontrado.", tenantId.toString(), null, tenantId);
                    return new BusinessException(ErrorCode.TENANT_NOT_FOUND);
                });
        if (tenant.getStatus() == Tenant.TenantStatus.INACTIVE) {
            log.warn("Falha na desativação do tenant: Tenant '{}' (ID: {}) já está inativo.", tenant.getNome(), tenantId);
            auditLogService.logError(ErrorCode.INVALID_REQUEST_PARAMETER.getCode(), "Desativação de tenant falhou: Tenant já inativo.", tenantId.toString(), tenantId, tenantId);
            throw new BusinessException(ErrorCode.INVALID_REQUEST_PARAMETER, "Tenant já está inativo.");
        }

        tenant.setStatus(Tenant.TenantStatus.INACTIVE);
        tenantRepository.save(tenant);
        log.info("Tenant '{}' (ID: {}) desativado com sucesso.", tenant.getNome(), tenantId);
        // TODO: [PRODUÇÃO] Em um ambiente de produção, considere desativar ou arquivar o esquema físico
        // no banco de dados (se for esquema-por-tenant e desejar remover dados).
    }

    /**
     * @brief Obtém um tenant pelo seu ID.
     * @param tenantId O ID do tenant.
     * @return O DTO do tenant.
     * @throws BusinessException se o tenant não for encontrado.
     */
    public TenantResponseDTO getTenantById(UUID tenantId) {
        log.debug("Buscando tenant pelo ID: {}", tenantId);
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    log.warn("Tenant não encontrado com ID: {}", tenantId);
                    auditLogService.logError(ErrorCode.TENANT_NOT_FOUND.getCode(), "Busca de tenant falhou: Tenant não encontrado.", tenantId.toString(), null, tenantId);
                    return new BusinessException(ErrorCode.TENANT_NOT_FOUND);
                });
        log.debug("Tenant encontrado: {} (ID: {})", tenant.getNome(), tenant.getId());
        return tenantMapper.toDto(tenant);
    }

    /**
     * @brief Obtém todos os tenants.
     * @return Uma lista de `TenantResponseDTO`.
     */
    public List<TenantResponseDTO> getAllTenants() {
        log.info("Buscando todos os tenants.");
        List<TenantResponseDTO> tenants = tenantRepository.findAll().stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
        log.info("Encontrados {} tenants.", tenants.size());
        return tenants;
    }

    /**
     * @brief Método fictício para simular a extração do ID do tenant de um token de
     * ativação.
     * Em um ambiente real, este token seria um JWT ou um UUID gerado e
     * armazenado temporariamente
     * para ativação. Por simplicidade, vamos assumir que o token é o próprio
     * UUID do tenant.
     *
     * @param token O token de ativação.
     * @return O UUID do tenant.
     * @throws BusinessException se o token não for um UUID válido.
     */
    private UUID parseTenantIdFromActivationToken(String token) {
        try {
            UUID parsedId = UUID.fromString(token);
            log.debug("Token de ativação parseado para UUID: {}", parsedId);
            return parsedId;
        } catch (IllegalArgumentException e) {
            log.error("Erro ao parsear token de ativação inválido: {}", token, e);
            auditLogService.logError(ErrorCode.INVALID_TOKEN.getCode(), "Parse de token de ativação falhou: Token inválido.", token, null, null);
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token de ativação inválido.");
        }
    }

    // Removido o método migrateSchemaForNewTenant, pois a responsabilidade de migração
    // de banco de dados não é deste microsserviço.
    // private void migrateSchemaForNewTenant(...) { ... }
}
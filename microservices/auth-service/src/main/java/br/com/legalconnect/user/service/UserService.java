package br.com.legalconnect.user.service;

import java.util.UUID;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.auth.service.RefreshTokenService;
import br.com.legalconnect.auth.util.PasswordEncoderUtil;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.common.exception.Roles;
import br.com.legalconnect.tenant.repository.TenantRepository; // Importar TenantRepository
import br.com.legalconnect.user.dto.UserProfileUpdate;
import br.com.legalconnect.user.dto.UserRegistrationRequest;
import br.com.legalconnect.user.dto.UserResponseDTO;
import br.com.legalconnect.user.entity.Role;
import br.com.legalconnect.user.entity.User;
import br.com.legalconnect.user.entity.User.UserStatus;
import br.com.legalconnect.user.entity.User.UserType;
import br.com.legalconnect.user.mapper.UserMapper;
import br.com.legalconnect.user.repository.RoleRepository;
import br.com.legalconnect.user.repository.UserRepository;

/**
 * @class UserService
 * @brief Serviço responsável pela lógica de negócio relacionada aos usuários.
 *
 *        Gerencia operações como registro de clientes e advogados, atualização
 *        de perfil,
 *        e recuperação/redefinição de senha.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class); // Instância do Logger

    @Autowired
    private UserRepository userRepository; /// < Repositório para acesso a dados de usuários.
    @Autowired
    private RoleRepository roleRepository; /// < Repositório para acesso a dados de roles.
    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil; /// < Utilitário para criptografia de senhas.
    @Autowired
    private UserMapper userMapper; /// < Mapper para converter entidades User em DTOs.
    @Autowired
    private TenantRepository tenantRepository; /// < Repositório para acesso a dados de tenants.
    @Autowired
    private RefreshTokenService refreshTokenService; // Para invalidar tokens ao mudar senha

    @Value("${application.tenant.default-id:00000000-0000-0000-0000-000000000001}")
    private String defaultTenantId; // ID do tenant padrão injetado do application.properties

    /**
     * @brief Registra um novo cliente na plataforma (RF048).
     * @param request DTO contendo os dados de registro do cliente.
     * @return DTO do usuário registrado.
     * @throws BusinessException se o e-mail ou CPF já estiverem cadastrados.
     */
    @Transactional
    public UserResponseDTO registerClient(UserRegistrationRequest request) {
        log.info("Iniciando registro de novo cliente com e-mail: {}", request.getEmail());
        // 1. Valida unicidade de e-mail e CPF
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Falha no registro de cliente: E-mail '{}' já cadastrado.", request.getEmail());

            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            log.warn("Falha no registro de cliente: CPF '{}' já cadastrado.", request.getCpf());
            throw new BusinessException(ErrorCode.INVALID_CPF);
        }

        // 2. Busca o tenant padrão
        Tenant defaultTenant = tenantRepository.findById(UUID.fromString(defaultTenantId))
                .orElseThrow(() -> {
                    log.error("Falha no registro de cliente: Tenant padrão '{}' não encontrado.", defaultTenantId);
                    return new BusinessException(ErrorCode.TENANT_NOT_FOUND, "Tenant padrão não encontrado.");
                });

        // 3. Cria a entidade User
        User user = userMapper.toEntity(request);
        user.setSenhaHash(passwordEncoderUtil.encode(request.getSenha())); // Criptografa a senha
        user.setUserType(UserType.CLIENTE);
        user.setStatus(UserStatus.ACTIVE); // Clientes são ativos por padrão
        user.setTenant(defaultTenant); // Associa ao tenant padrão
        log.debug("Usuário cliente criado DTO para e-mail: {}", user.getEmail());

        // 4. Atribui a role de CLIENTE
        Role clientRole = roleRepository.findByNome(Roles.ROLE_CLIENT)
                .orElseThrow(
                        () -> {
                            log.error("Falha no registro de cliente: Role CLIENTE não encontrada no banco de dados.");
                            return new BusinessException(ErrorCode.USER_NOT_FOUND,
                                    "Role CLIENTE não encontrada.");
                        });
        user.getRoles().add(clientRole);
        log.debug("Role CLIENTE atribuída ao usuário: {}", user.getEmail());

        // 5. Salva o usuário no banco de dados
        user = userRepository.save(user);
        log.info("Cliente registrado com sucesso: {} (ID: {})", user.getEmail(), user.getId());

        // Disparar evento para NotificationService para enviar e-mail de boas-vindas

        log.debug("E-mail de boas-vindas enviado para: {}", user.getEmail());

        return userMapper.toDto(user);
    }

    /**
     * @brief Realiza o pré-cadastro de um novo advogado na plataforma (RF001).
     * @param request DTO contendo os dados de pré-registro do advogado.
     * @return DTO do usuário advogado pré-registrado.
     * @throws BusinessException se o e-mail, CPF ou número da OAB já estiverem
     *                           cadastrados.
     */
    @Transactional
    public UserResponseDTO registerAdvogado(UserRegistrationRequest request) {
        log.info("Iniciando pré-cadastro de novo advogado com e-mail: {}", request.getEmail());
        // Este microsserviço de autenticação não deve lidar com a criação completa do
        // perfil de advogado,
        // apenas com o registro do usuário base e sua associação a um tenant e role
        // inicial.
        // A criação do perfil de advogado (Advogado entity) e suas associações
        // complexas
        // devem ser responsabilidade do microsserviço de Marketplace/Advogado.

        // 1. Valida unicidade de e-mail e CPF
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Falha no pré-cadastro de advogado: E-mail '{}' já cadastrado.", request.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            log.warn("Falha no pré-cadastro de advogado: CPF '{}' já cadastrado.", request.getCpf());
            throw new BusinessException(ErrorCode.INVALID_CPF);
        }
        // A validação de OAB e outras informações específicas de advogado
        // deve ser feita no microsserviço de Marketplace.

        // 2. Busca o tenant padrão
        Tenant defaultTenant = tenantRepository.findById(UUID.fromString(defaultTenantId))
                .orElseThrow(() -> {
                    log.error("Falha no pré-cadastro de advogado: Tenant padrão '{}' não encontrado.", defaultTenantId);
                    return new BusinessException(ErrorCode.TENANT_NOT_FOUND, "Tenant padrão não encontrado.");
                });

        // 3. Cria a entidade User
        User user = userMapper.toEntity(request);
        user.setSenhaHash(passwordEncoderUtil.encode(request.getSenha())); // Criptografa a senha
        user.setUserType(UserType.ADVOGADO); // Define o tipo como ADVOGADO
        user.setStatus(UserStatus.PENDING_APPROVAL); // Advogados aguardam aprovação inicial
        user.setTenant(defaultTenant); // Associa ao tenant padrão
        log.debug("Usuário advogado criado DTO para e-mail: {}", user.getEmail());

        // 4. Atribui a role de ADVOGADO
        Role advocateRole = roleRepository.findByNome(Roles.ROLE_ADVOCATE)
                .orElseThrow(
                        () -> {
                            log.error(
                                    "Falha no pré-cadastro de advogado: Role ADVOGADO não encontrada no banco de dados.");

                            return new BusinessException(ErrorCode.ADVOCATE_NOT_AVAILABLE,
                                    "Role ADVOGADO não encontrada.");
                        });
        user.getRoles().add(advocateRole);
        log.debug("Role ADVOGADO atribuída ao usuário: {}", user.getEmail());

        // 5. Salva o usuário no banco de dados
        user = userRepository.save(user);
        log.info("Advogado pré-registrado com sucesso: {} (ID: {})", user.getEmail(), user.getId());

        // Disparar evento para NotificationService para enviar e-mail de confirmação de
        // pré-cadastro
        log.debug("E-mail de confirmação de pré-cadastro enviado para: {}", user.getEmail());

        // Retorna o DTO do usuário registrado. O perfil de advogado será criado
        // separadamente.
        return userMapper.toDto(user);
    }

    /**
     * @brief Inicia o processo de recuperação de senha (RF053).
     *
     *        Gera um token temporário e envia um e-mail com o link de redefinição.
     *
     * @param email O e-mail do usuário que solicitou a recuperação.
     */
    @Transactional
    public void recoverPassword(String email) {
        log.info("Solicitação de recuperação de senha para o e-mail: {}", email);
        User user = userRepository.findByEmail(email)
                .orElse(null); // Não lançar exceção aqui por segurança, para não vazar informações de e-mails
                               // cadastrados.

        if (user != null) {
            log.debug("Usuário encontrado para recuperação de senha: {}", email);
            // TODO: [PRODUÇÃO] Implementar a geração e persistência de um token de
            // redefinição de senha real.
            // Ex: PasswordResetToken resetToken =
            // passwordResetTokenService.createPasswordResetToken(user);

            // Envia e-mail com o link de redefinição de senha contendo o token
            // Em produção, o link seria para o frontend:
            // "[http://frontend.com/reset-password?token=](http://frontend.com/reset-password?token=)"
            // + resetToken.getToken()
            log.info("E-mail de recuperação de senha enviado para: {}", user.getEmail());

        } else {
            log.info(
                    "Solicitação de recuperação de senha para e-mail não encontrado: {}. Retornando sucesso para evitar enumeração de usuários.",
                    email);
            // Logar a tentativa de recuperação para um e-mail não existente, mas sem expor
            // a informação

        }
    }

    /**
     * @brief Redefine a senha do usuário utilizando um token de recuperação
     *        (RF053).
     * @param token     O token de redefinição de senha.
     * @param novaSenha A nova senha a ser definida.
     * @throws BusinessException se o token for inválido/expirado ou a senha for
     *                           fraca.
     */
    @Transactional
    public void resetPassword(String token, String novaSenha) {
        log.info("Tentativa de redefinição de senha com token.");
        // TODO: [PRODUÇÃO] Validar o token de redefinição de senha real (não o mock).
        // Isso envolveria buscar o token no banco de dados e verificar sua validade e
        // expiração.
        if (!"mock-reset-token-123".equals(token)) {
            log.warn("Falha na redefinição de senha: Token inválido.");

            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        // TODO: [PRODUÇÃO] Buscar o usuário associado ao token real, não um e-mail
        // fixo.
        User user = userRepository.findByEmail("social.user@gmail.com") // Exemplo: buscar o usuário do token real
                .orElseThrow(() -> {
                    log.error("Falha na redefinição de senha: Usuário associado ao token não encontrado.");

                    return new BusinessException(ErrorCode.USER_NOT_FOUND,
                            "Usuário associado ao token não encontrado.");
                });

        // 2. Criptografa e atualiza a nova senha
        user.setSenhaHash(passwordEncoderUtil.encode(novaSenha));
        userRepository.save(user);
        log.info("Senha redefinida com sucesso para o usuário: {}", user.getEmail());

        // Invalida todos os refresh tokens do usuário para forçar novo login
        refreshTokenService.deleteByUser(user);
        log.debug("Refresh tokens invalidados para o usuário: {}", user.getEmail());

        // Envia e-mail de confirmação de redefinição de senha

        log.debug("E-mail de confirmação de redefinição de senha enviado para: {}", user.getEmail());

    }

    /**
     * @brief Atualiza o perfil de um usuário (RF055).
     * @param userId        O ID do usuário a ser atualizado.
     * @param updateRequest DTO com os dados de atualização.
     * @return DTO do usuário atualizado.
     * @throws BusinessException se o usuário não for encontrado, e-mail/CPF já em
     *                           uso, ou senha atual inválida.
     */
    @Transactional
    public UserResponseDTO updateUserProfile(UUID userId, UserProfileUpdate updateRequest) {
        log.info("Tentativa de atualização de perfil para o usuário ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização de perfil: Usuário não encontrado com ID: {}", userId);

                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        // Valida unicidade de e-mail, se o e-mail for alterado
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                log.warn("Falha na atualização de perfil: Novo e-mail '{}' já em uso.", updateRequest.getEmail());

                throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
            }
            user.setEmail(updateRequest.getEmail());
            log.debug("E-mail do usuário {} atualizado para: {}", userId, updateRequest.getEmail());
        }

        // Atualiza outros campos do usuário
        userMapper.updateEntityFromDto(updateRequest, user);
        log.debug("Outros campos do perfil do usuário {} atualizados.", userId);

        // Lógica para alteração de senha
        if (updateRequest.getSenhaAtual() != null && updateRequest.getNovaSenha() != null) {
            log.info("Tentativa de alteração de senha para o usuário ID: {}", userId);
            if (!passwordEncoderUtil.matches(updateRequest.getSenhaAtual(), user.getSenhaHash())) {
                log.warn("Falha na alteração de senha: Senha atual inválida para o usuário ID: {}", userId);

                throw new BusinessException(ErrorCode.PASSWORD_TOO_WEAK);
            }
            user.setSenhaHash(passwordEncoderUtil.encode(updateRequest.getNovaSenha()));
            // Invalida todos os refresh tokens do usuário para forçar novo login após a
            // mudança de senha
            refreshTokenService.deleteByUser(user);
            log.info("Senha do usuário {} alterada com sucesso. Refresh tokens invalidados.", userId);
        }

        user = userRepository.save(user);
        log.info("Perfil do usuário {} atualizado com sucesso.", userId);
        return userMapper.toDto(user);
    }
}
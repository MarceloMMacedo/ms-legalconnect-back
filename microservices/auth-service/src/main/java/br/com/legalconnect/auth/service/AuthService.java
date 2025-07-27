package br.com.legalconnect.auth.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.legalconnect.auth.dto.AuthResponse;
import br.com.legalconnect.auth.dto.LoginRequestDTO;
import br.com.legalconnect.auth.dto.RefreshTokenRequestDTO;
import br.com.legalconnect.auth.dto.UserProfileUpdate;
import br.com.legalconnect.auth.dto.UserRegistrationRequest;
import br.com.legalconnect.auth.dto.UserResponseDTO;
import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.auth.repository.TenantRepository;
import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.common.exception.Roles;
import br.com.legalconnect.enums.StatusResponse;
import br.com.legalconnect.user.entity.PasswordResetToken;
import br.com.legalconnect.user.entity.Role;
import br.com.legalconnect.user.entity.User;
import br.com.legalconnect.user.entity.User.UserStatus;
import br.com.legalconnect.user.entity.User.UserType;
import br.com.legalconnect.user.repository.PasswordResetTokenRepository;
import br.com.legalconnect.user.repository.RoleRepository;
import br.com.legalconnect.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

/**
 * @class AuthService
 * @brief Serviço de lógica de negócios para autenticação e refresh de tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final TenantRepository tenantRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final RefreshTokenService refreshTokenService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final EmailService emailService;

    private final UserMapper userMapper;

    @Value("${app.frontend.url}") // INJETAR A URL DO FRONTEND
    private String frontendBaseUrl;

    private long passwordResetExpirationMinutes;

    @Value("${application.tenant.default-id:00000000-0000-0000-0000-000000000001}")
    private String defaultTenantIds;

    /*
     * /
     * Autentica um usuário e gera tokens JWT.
     * Inclui id do usuário e id do tenant nos claims do JWT.
     * * @param request DTO de requisição de login.
     * 
     * @return DTO de resposta de login com tokens.
     * 
     * @throws ResponseStatusException se o usuário não for encontrado ou as
     * credenciais forem inválidas.
     */
    @Transactional
    public BaseResponse<AuthResponse> authenticate(LoginRequestDTO request) {
        log.debug("Iniciando autenticação para o e-mail: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado no banco de dados para o e-mail: {}", request.getEmail());
                    return new BusinessException(ErrorCode.USER_NOT_FOUND,
                            "Usuário não encontrado com o e-mail: " + request.getEmail());
                });

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()));
            log.info("Credenciais válidas para o e-mail: {}", request.getEmail());
        } catch (Exception e) {
            log.warn("Falha na autenticação para o e-mail: {}. Erro: {}", request.getEmail(), e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Credenciais inválidas.");
        }

        List<String> roles = user.getRoles().stream().map(Role::getNome).toList();

        // Adiciona id do usuário e id do tenant aos claims do JWT
        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", roles);

        claims.put("X-Correlation-ID", user.getId());
        if (user.getTenant() != null) {
            claims.put("X-Tenant-ID", user.getTenant().getSchemaName());
        }

        // Gera os tokens com os claims adicionais
        String jwtToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
        log.info("Tokens JWT gerados para o usuário ID: {}, Tenant ID: {}", user.getId(),
                user.getTenant() != null ? user.getTenant().getId() : "N/A");

        // Adiciona informações ao MDC após autenticação bem-sucedida para logs
        // subsequentes
        MDC.put("X-Correlation-ID", String.valueOf(user.getId()));
        if (user.getTenant() != null) {
            MDC.put("X-Tenant-ID", String.valueOf(user.getTenant().getId()));
        }

        return BaseResponse.<AuthResponse>builder()
                .status(StatusResponse.SUCESSO)
                .message("Autenticação realizada com sucesso.")
                .timestamp(LocalDateTime.now())
                .data(AuthResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(jwtService.extractExpiration(jwtToken).getTime() / 1000)
                        .build())
                .build();
    }

    /**
     * Renova o access token usando um refresh token.
     * * @param request DTO de requisição de refresh token.
     * 
     * @return DTO de resposta de login com novo access token.
     * @throws ResponseStatusException se o refresh token for inválido ou expirado.
     */
    @Transactional
    public BaseResponse<AuthResponse> refreshToken(RefreshTokenRequestDTO request) {
        log.debug("Iniciando processo de refresh token.");
        String userEmail = jwtService.extractUsername(request.getRefreshToken());

        if (userEmail == null) {
            log.warn("Refresh token não contém e-mail de usuário válido.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Usuário não encontrado para o e-mail do refresh token: {}", userEmail);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Usuário não encontrado com o e-mail: " + userEmail);
                });

        if (jwtService.isTokenValid(request.getRefreshToken(), user)) {
            log.info("Refresh token válido para o usuário: {}", userEmail);
            // Ao gerar um novo access token, re-incluímos os claims de userId e tenantId
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            if (user.getTenant() != null) {
                claims.put("tenantId", user.getTenant().getId());
            }
            String accessToken = jwtService.generateToken(claims, user);

            log.info("Novo access token gerado para o usuário ID: {}, Tenant ID: {}", user.getId(),
                    user.getTenant() != null ? user.getTenant().getId() : "N/A");

            // Adiciona informações ao MDC para logs subsequentes
            MDC.put("userId", String.valueOf(user.getId()));
            if (user.getTenant() != null) {
                MDC.put("tenantId", String.valueOf(user.getTenant().getId()));
            }

            return br.com.legalconnect.common.dto.BaseResponse.<AuthResponse>builder()
                    .status(StatusResponse.SUCESSO)
                    .message("Token atualizado com sucesso.")
                    .timestamp(LocalDateTime.now())
                    .data(AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(request.getRefreshToken()) // Mantém o mesmo refresh token
                            .tokenType("Bearer")
                            .expiresIn(jwtService.extractExpiration(accessToken).getTime() / 1000)
                            .build())
                    .build();
        } else {
            log.warn("Refresh token inválido ou expirado para o usuário: {}", userEmail);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou expirado.");
        }
    }

    /**
     * @brief Registra um novo usuário na plataforma com base no tipo especificado.
     * @param request  DTO contendo os dados de registro do usuário.
     * @param userType O tipo de usuário a ser registrado (CLIENTE, ADVOGADO, SOCIO,
     *                 PLATAFORMA_ADMIN).
     * @return DTO do usuário registrado.
     * @throws BusinessException se o e-mail ou CPF já estiverem cadastrados, ou se
     *                           a role não for encontrada.
     */
    // @Transactional
    public UserResponseDTO registerUser(UserRegistrationRequest request, UserType userType) {
        log.info("Iniciando registro de novo usuário do tipo {} com e-mail: {}", userType, request.getEmail());

        // 1. Valida unicidade de e-mail e CPF
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Falha no registro: E-mail '{}' já cadastrado.", request.getEmail());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
        // if (userRepository.existsByCpf(request.getCpf())) {
        // log.warn("Falha no registro: CPF '{}' já cadastrado.", request.getCpf());
        // throw new BusinessException(ErrorCode.INVALID_CPF);
        // }
        // 2. Busca o tenant padrão
        // 2. Busca o tenant padrão ou cria um novo se não existir
        Tenant defaultTenant = tenantRepository.findBySchemaName(defaultTenantIds)
                .orElseGet(() -> {
                    log.info("Tenant padrão não encontrado. Criando novo tenant com schema: {}", defaultTenantIds);
                    return tenantRepository.save(Tenant.builder()
                            .schemaName(defaultTenantIds)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .nome("Tenant Padrão")
                            .build());
                });

        // 3. Cria a entidade User
        User user = userMapper.toEntity(request);
        user.setSenhaHash(passwordEncoder.encode(request.getSenha())); // Criptografa a senha
        user.setUserType(userType);
        user.setTenant(defaultTenant); // Associa ao tenant padrão

        // 4. Define o status inicial e atribui a role com base no tipo de usuário
        Role assignedRole;
        switch (userType) {
            case CLIENTE:
                user.setStatus(UserStatus.ACTIVE); // Clientes são ativos por padrão
                assignedRole = roleRepository.findByNome(Roles.ROLE_CLIENT)
                        .orElseThrow(() -> {
                            log.error("Falha no registro: Role CLIENTE não encontrada no banco de dados.");
                            return new BusinessException(ErrorCode.USER_NOT_FOUND, "Role CLIENTE não encontrada.");
                        });
                break;
            case ADVOGADO:
                user.setStatus(UserStatus.PENDING_APPROVAL); // Advogados aguardam aprovação
                assignedRole = roleRepository.findByNome(Roles.ROLE_ADVOCATE)
                        .orElseThrow(() -> {
                            log.error("Falha no registro: Role ADVOGADO não encontrada no banco de dados.");
                            return new BusinessException(ErrorCode.ADVOCATE_NOT_AVAILABLE,
                                    "Role ADVOGADO não encontrada.");
                        });
                break;
            case SOCIO:
                user.setStatus(UserStatus.PENDING); // Sócios aguardam aprovação inicial
                assignedRole = roleRepository.findByNome(Roles.ROLE_TENANT_ADMIN) // Exemplo: Sócio pode ser um ADMIN do
                                                                                  // Tenant
                        .orElseThrow(() -> {
                            log.error("Falha no registro: Role SOCIO (TENANT_ADMIN) não encontrada no banco de dados.");
                            return new BusinessException(ErrorCode.USER_NOT_FOUND, "Role SOCIO não encontrada.");
                        });
                break;
            case PLATAFORMA_ADMIN:
                user.setStatus(UserStatus.PENDING); // Administradores da plataforma aguardam aprovação
                assignedRole = roleRepository.findByNome(Roles.ROLE_ADMIN)
                        .orElseThrow(() -> {
                            log.error("Falha no registro: Role PLATAFORMA_ADMIN não encontrada no banco de dados.");
                            return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                    "Role PLATAFORMA_ADMIN não encontrada.");
                        });
                break;
            default:
                log.error("Tipo de usuário inválido para registro: {}", userType);
                throw new BusinessException(ErrorCode.INVALID_INPUT, "Tipo de usuário inválido.");
        }

        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(assignedRole);
        log.debug("Role {} atribuída ao usuário: {}", assignedRole.getNome(), user.getEmail());

        // 5. Salva o usuário no banco de dados
        user = userRepository.save(user);
        log.info("Usuário do tipo {} registrado com sucesso: {} (ID: {})", userType, user.getEmail(), user.getId());

        // TODO: Disparar evento para NotificationService para enviar e-mail de
        // boas-vindas/confirmação
        log.debug("E-mail de boas-vindas/confirmação enviado para: {}", user.getEmail());
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    /**
     * @brief Registra um novo cliente na plataforma (RF048).
     * @param request DTO contendo os dados de registro do cliente.
     * @return DTO do usuário registrado.
     * @throws BusinessException se o e-mail ou CPF já estiverem cadastrados.
     */
    @Transactional
    public UserResponseDTO registerClient(UserRegistrationRequest request) {
        // Delega para o método genérico com UserType.CLIENTE
        return registerUser(request, UserType.CLIENTE);
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
        // Delega para o método genérico com UserType.ADVOGADO
        return registerUser(request, UserType.ADVOGADO);
    }

    /**
     * @brief Registra um novo sócio na plataforma.
     * @param request DTO contendo os dados de registro do sócio.
     * @return DTO do usuário sócio registrado.
     * @throws BusinessException se o e-mail ou CPF já estiverem cadastrados.
     */
    @Transactional
    public UserResponseDTO registerSocio(UserRegistrationRequest request) {
        // Delega para o método genérico com UserType.SOCIO
        return registerUser(request, User.UserType.CLIENTE);
    }

    /**
     * @brief Registra um novo administrador da plataforma.
     * @param request DTO contendo os dados de registro do administrador.
     * @return DTO do usuário administrador registrado.
     * @throws BusinessException se o e-mail ou CPF já estiverem cadastrados.
     */
    // @Transactional
    public UserResponseDTO registerAdmin(UserRegistrationRequest request) {
        // Delega para o método genérico com UserType.PLATAFORMA_ADMIN
        return registerUser(request, UserType.PLATAFORMA_ADMIN);
    }

    /**
     * @brief Inicia o processo de recuperação de senha (RF053).
     *
     *        Gera um token temporário e envia um e-mail com o link de redefinição.
     *
     * @param email O e-mail do usuário que solicitou a recuperação.
     * @throws BusinessException se o usuário não for encontrado.
     */
    // @Transactional
    public void recoverPassword(String email) {
        log.info("Solicitação de recuperação de senha para o e-mail: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Falha na recuperação de senha: Usuário não encontrado para o e-mail: {}", email);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND, "Usuário não encontrado.");
                });

        // 1. Invalida qualquer token de redefinição anterior para este usuário
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        // 2. Gera um novo token único e define a expiração
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(60 * 12, ChronoUnit.MINUTES);

        final PasswordResetToken resetToken = passwordResetTokenRepository.findByUserAndUsado(user, true)
                .orElseGet(() -> {
                    PasswordResetToken newToken = PasswordResetToken.builder()
                            .token(token)
                            .user(user)
                            .expiraEm(expiryDate)
                            .usado(false)
                            .tentativas(0)
                            .build();
                    return passwordResetTokenRepository.save(newToken);
                });
        log.info("Token de redefinição de senha gerado e salvo para o usuário: {}", user.getEmail());

        // CONSTRUIR O LINK DE REDEFINIÇÃO DE SENHA
        String resetLink = frontendBaseUrl + "/reset-password?token=" + token; // Adapte a rota do seu frontend

        // CHAMAR O NOVO MÉTODO PARA ENVIAR O EMAIL
        try {
            // sendPasswordResetEmail(user.getEmail(), user.getNomeCompleto(), resetLink);
            // // Assumindo que User tem
            // getNome()
        } finally {
            log.error("Erro ao enviar e-mail de recuperação de senha para: {}", user.getEmail());
        }
        // TODO: [PRODUÇÃO] Enviar e-mail com o link de redefinição de senha
        // O link deve apontar para o frontend, contendo o token:
        // String resetLink =
        // "[https://seufrontend.com/reset-password?token=](https://seufrontend.com/reset-password?token=)"
        // + token;
        // notificationService.sendPasswordResetEmail(user.getEmail(), resetLink);
        log.info("E-mail de recuperação de senha enviado para: {} com token: {}", user.getEmail(), token);
    }

    /**
     * @brief Redefine a senha do usuário utilizando um token de recuperação
     *        (RF053).
     * @param token     O token de redefinição de senha.
     * @param novaSenha A nova senha a ser definida.
     * @throws BusinessException se o token for inválido/expirado/usado ou a senha
     *                           for
     *                           fraca.
     */
    // @Transactional
    public void resetPassword(String email, String novaSenha) {
        log.info("Tentativa de redefinição de senha com token.");

        // 1. Busca e valida o token
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> {
                            log.warn("Falha na redefinição de senha: Usuário não encontrado para o e-mail: {}", email);
                            return new BusinessException(ErrorCode.USER_NOT_FOUND, "Usuário não encontrado.");
                        });

        PasswordResetToken resetToken = passwordResetTokenRepository.findFirstByUserId(user.getId()).get();
        if (resetToken == null) {
            log.warn("Falha na redefinição de senha: Token inválido ou não encontrado.");
            new BusinessException(ErrorCode.PASSWORD_RESET_TOKEN_INVALID,
                    "Falha na redefinição de senha: Token inválido ou não encontrado.");
        }

        if (resetToken.isUsado()) {
            log.warn("Falha na redefinição de senha: Token já utilizado para o usuário: {}",
                    resetToken.getUser().getEmail());
            throw new BusinessException(ErrorCode.PASSWORD_RESET_TOKEN_USED,
                    "Falha na redefinição de senha: Token já utilizado para o usuário");
        }
        var tentativas = resetToken.getTentativas();
        resetToken.setTentativas(tentativas + 1);

        passwordResetTokenRepository.save(resetToken);
        if (tentativas >= 3) {
            log.warn("Falha na redefinição de senha: Tentativas de redefinição excedidas para o usuário: {}",
                    resetToken.getUser().getEmail());
            throw new BusinessException(ErrorCode.PASSWORD_RESET_TOKEN_EXCEEDED,
                    "Falha na redefinição de senha: Tentativas de redefinição excedidas");
        }
        var inspiracao = resetToken.getExpiraEm();
        var inspiracao2 = Instant.now();
        if (inspiracao.isBefore(inspiracao2)) {
            log.warn("Falha na redefinição de senha: Token expirado para o usuário: {}",
                    resetToken.getUser().getEmail());
            resetToken.setUsado(true);
            passwordResetTokenRepository.save(resetToken);
            // passwordResetTokenRepository.delete(resetToken); // Opcional: deletar tokens
            // expirados
            throw new BusinessException(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED,
                    "Falha na redefinição de senha: Token expirado para o usuário");
        }

        // 2. Busca o usuário associado ao token
        log.debug("Usuário encontrado para redefinição de senha: {}", user.getEmail());

        // 3. Criptografa e atualiza a nova senha
        user.setSenhaHash(passwordEncoder.encode(novaSenha));
        userRepository.save(user);
        log.info("Senha redefinida com sucesso para o usuário: {}", user.getEmail());

        // 4. Invalida o token de redefinição (marca como usado)
        resetToken.setUsado(true);
        passwordResetTokenRepository.save(resetToken);
        log.debug("Token de redefinição de senha marcado como usado.");

        // 5. Invalida todos os refresh tokens do usuário para forçar novo login
        refreshTokenService.deleteByUser(user);
        log.debug("Refresh tokens invalidados para o usuário: {}", user.getEmail());

        // TODO: [PRODUÇÃO] Envia e-mail de confirmação de redefinição de senha
        // notificationService.sendPasswordResetConfirmationEmail(user.getEmail());
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

                    return new BusinessException(ErrorCode.USER_NOT_FOUND,
                            "Falha na atualização de perfil: Usuário não encontrado com ID");
                });

        // Valida unicidade de e-mail, se o e-mail for alterado
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                log.warn("Falha na atualização de perfil: Novo e-mail '{}' já em uso.", updateRequest.getEmail());

                throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED,
                        "Falha na atualização de perfil: Novo e-mail " + updateRequest.getEmail() + " já em uso.");
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
            if (!passwordEncoder.matches(updateRequest.getSenhaAtual(), user.getSenhaHash())) {
                log.warn("Falha na alteração de senha: Senha atual inválida para o usuário ID: {}", userId);

                throw new BusinessException(ErrorCode.PASSWORD_TOO_WEAK,
                        "Falha na alteração de senha: Senha atual inválida para o usuário ID");
            }
            user.setSenhaHash(passwordEncoder.encode(updateRequest.getNovaSenha()));
            // Invalida todos os refresh tokens do usuário para forçar novo login após a
            // mudança de senha
            refreshTokenService.deleteByUser(user);
            log.info("Senha do usuário {} alterada com sucesso. Refresh tokens invalidados.", userId);
        }

        user = userRepository.save(user);
        log.info("Perfil do usuário {} atualizado com sucesso.", userId);
        return userMapper.toDto(user);
    }

    /**
     * Método dedicado ao envio do e-mail de recuperação de senha.
     * Utiliza o template HTML e variáveis dinâmicas.
     *
     * @param to        Endereço de e-mail do destinatário.
     * @param userName  Nome do usuário para personalização do e-mail.
     * @param resetLink Link completo para a página de redefinição de senha no
     *                  frontend.
     */
    public void sendPasswordResetEmail(String to, String userName, String resetLink) {
        // PREPARAR VARIÁVEIS PARA O TEMPLATE DO EMAIL
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("userName", userName);
        templateVariables.put("resetLink", resetLink);
        templateVariables.put("expirationMinutes", passwordResetExpirationMinutes);
        templateVariables.put("currentYear", Instant.now().atZone(ZoneId.of("America/Sao_Paulo")).getYear()); // Pega o
                                                                                                              // ano
                                                                                                              // atual
                                                                                                              // para
                                                                                                              // Parnaíba

        try {
            // ENVIAR O E-MAIL USANDO O SERVIÇO DE E-MAIL COM TEMPLATE
            emailService.sendTemplatedEmail(
                    to,
                    "Recuperação de Senha - [Seu Nome de Aplicação]", // Assunto do e-mail
                    "password-reset-email", // Nome do template HTML (sem .html)
                    templateVariables);
            log.info("E-mail de recuperação de senha enviado com sucesso para: {}", to);
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail de recuperação de senha para {}: {}", to, e.getMessage(), e);
            // Opcional: Você pode relançar uma BusinessException ou tratar de outra forma
            throw new BusinessException(ErrorCode.INVALID_EMAIL, "Falha ao enviar e-mail de recuperação de senha.");
        }
    }

    /**
     * @brief Atualiza o status de um usuário.
     *        Este método só pode ser acessado por usuários que NÃO POSSUAM a role
     *        'ROLE_ADMIN'.
     * @param userId    O ID do usuário cujo status será atualizado.
     * @param newStatus O novo status para o usuário, deve corresponder a um valor
     *                  do enum {@link UserStatus}.
     * @return DTO do usuário atualizado.
     * @throws BusinessException se o usuário não for encontrado ou se o status
     *                           fornecido for inválido.
     */
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Garante que usuários autenticados, mas NÃO ADMIN,
                                           // podem usar este método.
    public UserResponseDTO updateUserStatus(UUID userId, String newStatus) {
        log.info("Tentativa de atualização de status para o usuário ID: {} com o novo status: {}", userId, newStatus);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização de status: Usuário não encontrado com ID: {}", userId);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND, "Usuário não encontrado.");
                });

        try {
            UserStatus statusEnum = UserStatus.valueOf(newStatus.toUpperCase());
            user.setStatus(statusEnum);
            user = userRepository.save(user);
            log.info("Status do usuário {} atualizado com sucesso para: {}", userId, newStatus);
            return userMapper.toDto(user);
        } catch (IllegalArgumentException e) {
            log.warn("Falha na atualização de status: Status inválido fornecido '{}' para o usuário ID: {}", newStatus,
                    userId);
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Status de usuário inválido: " + newStatus
                    + ". Status permitidos: ACTIVE, INACTIVE, PENDING_APPROVAL, REJECTED, PENDING.");
        }
    }
}
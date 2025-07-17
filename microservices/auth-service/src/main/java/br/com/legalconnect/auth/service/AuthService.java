package br.com.legalconnect.auth.service;

import org.slf4j.Logger; // Importação para Logger
import org.slf4j.LoggerFactory; // Importação para LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.auth.dto.AuthResponse;
import br.com.legalconnect.auth.dto.LoginRequest;
import br.com.legalconnect.auth.dto.RefreshTokenRequest;
import br.com.legalconnect.auth.entity.RefreshToken;
import br.com.legalconnect.auth.security.CustomUserDetails;
import br.com.legalconnect.auth.security.JwtUtil;
import br.com.legalconnect.auth.util.PasswordEncoderUtil;
import br.com.legalconnect.common.exception.BusinessException; // Importação para BusinessException
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.user.entity.User; // Importação para a entidade User
import br.com.legalconnect.user.repository.UserRepository;

/**
 * @class AuthService
 * @brief Serviço responsável pela lógica de autenticação e autorização de
 *        usuários.
 *
 *        Gerencia operações como login, geração e validação de JWTs e Refresh
 *        Tokens.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class); // Instância do Logger

    @Autowired
    private UserRepository userRepository; /// < Repositório para acesso a dados de usuários.
    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil; /// < Utilitário para criptografia de senhas.
    @Autowired
    private JwtUtil jwtUtil; /// < Utilitário para manipulação de JWTs.
    @Autowired
    private RefreshTokenService refreshTokenService; /// < Serviço para gerenciamento de Refresh Tokens.

    /**
     * @brief Realiza o login de um usuário na plataforma.
     *
     *        Valida as credenciais (e-mail e senha) e, se válidas, gera um JWT e um
     *        Refresh Token.
     *
     * @param request DTO contendo e-mail e senha do usuário.
     * @return Um `AuthResponse` com o access token e refresh token.
     * @throws BusinessException se o usuário não for encontrado, estiver inativo ou
     *                           as credenciais forem inválidas.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Tentativa de login para o e-mail: {}", request.getEmail());

        // 1. Busca o usuário pelo e-mail
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Falha no login: Usuário não encontrado para o e-mail: {}", request.getEmail());
                    // auditLogService.logError(ErrorCode.USER_NOT_FOUND.getCode(),
                    // "Login falhou: Usuário não encontrado.", request.getEmail(), null, null);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND, "Usuário ou senha inválidos.");
                });

        // 2. Verifica se o usuário está ativo
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            log.warn("Falha no login: Usuário inativo para o e-mail: {}", request.getEmail());
            // auditLogService.logError(ErrorCode.USER_INACTIVE.getCode(), "Login falhou:
            // Usuário inativo.",
            // request.getEmail(), user.getId(), user.getTenant().getId());
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "Usuário inativo. Favor contatar o suporte.");
        }

        // 3. Compara a senha fornecida com a senha criptografada armazenada
        if (!passwordEncoderUtil.matches(request.getSenha(), user.getSenhaHash())) {
            log.warn("Falha no login: Credenciais inválidas para o e-mail: {}", request.getEmail());

            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Usuário ou senha inválidos.");
        }

        // 4. Gera o Access Token (JWT)
        String accessToken = jwtUtil.generateToken(new CustomUserDetails(user));
        log.debug("Access Token gerado para o usuário: {}", user.getEmail());

        // 5. Gera e armazena o Refresh Token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        log.debug("Refresh Token gerado para o usuário: {}", user.getEmail());

        log.info("Login bem-sucedido para o usuário: {}", user.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * @brief Renova o Access Token utilizando um Refresh Token válido.
     *
     * @param request DTO contendo o Refresh Token.
     * @return Um `AuthResponse` com um novo access token e o mesmo refresh token.
     * @throws BusinessException se o refresh token for inválido ou expirado.
     */
    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        log.info("Tentativa de refresh de token.");
        // 1. Valida o Refresh Token
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> {
                    log.warn("Falha no refresh token: Token não encontrado.");

                    return new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh token inválido.");
                });

        // 2. Verifica se o Refresh Token expirou
        refreshTokenService.verifyExpiration(refreshToken);
        log.debug("Refresh Token verificado e válido.");

        // 3. Busca o usuário associado ao Refresh Token
        User user = refreshToken.getUser();
        log.debug("Usuário associado ao refresh token: {}", user.getEmail());

        // 4. Gera um novo Access Token
        String newAccessToken = jwtUtil.generateToken(new CustomUserDetails(user));
        log.debug("Novo Access Token gerado para o usuário: {}", user.getEmail());

        log.info("Refresh de token bem-sucedido para o usuário: {}", user.getEmail());
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * @brief Realiza o logout do usuário, invalidando o Refresh Token.
     *
     * @param refreshToken O Refresh Token a ser invalidado.
     * @throws BusinessException se o refresh token não for encontrado.
     */
    @Transactional
    public void logout(String refreshToken) {
        log.info("Tentativa de logout para o refresh token.");
        try {
            refreshTokenService.deleteByToken(refreshToken);
            log.info("Logout bem-sucedido para o refresh token.");
        } catch (BusinessException e) {
            log.warn("Falha no logout: {}", e.getMessage());

            throw e;
        }
    }

    /**
     * @brief Realiza o login/cadastro via autenticação social (Google, LinkedIn).
     *
     * @param request DTO contendo o provedor e o token social.
     * @return Um `AuthResponse` com o access token e refresh token.
     * @throws BusinessException se o token social for inválido ou o provedor não
     *                           for suportado.
     * 
     * @Transactional
     *                public AuthResponse socialLogin(SocialLoginRequest request) {
     *                log.info("Tentativa de login social para o provedor: {}",
     *                request.getProvider());
     * 
     *                // TODO: [PRODUÇÃO] Implementar integração real com provedores
     *                OAuth2 (Google,
     *                // LinkedIn).
     *                // Esta é uma implementação mock. Em um ambiente de produção,
     *                você faria
     *                // chamadas
     *                // para as APIs dos provedores para validar o token e obter os
     *                dados do usuário.
     *                // Isso pode envolver bibliotecas específicas para
     *                OAuth2/OIDC.
     * 
     *                if ("GOOGLE".equalsIgnoreCase(request.getProvider())) {
     *                log.debug("Processando login social com Google.");
     *                // Simula validação do token Google
     *                if (!"valid-google-token-123".equals(request.getToken())) {
     *                log.warn("Falha no login social Google: Token inválido.");
     * 
     *                throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token
     *                Google inválido.");
     *                }
     *                // Simula busca ou criação de usuário
     *                User user =
     *                userRepository.findByEmail("social.user@gmail.com").orElseGet(()
     *                -> {
     *                log.info("Criando novo usuário para login social Google:
     *                social.user@gmail.com");
     *                // Para social login, você precisará de um tenant padrão para
     *                associar o usuário
     *                // Em um ambiente de produção, este "public" schemaName pode
     *                ser configurável
     *                // ou o tenant padrão pode ser buscado de outra forma.
     *                Tenant defaultTenant =
     *                tenantRepository.findBySchemaName("public")
     *                .orElseThrow(() -> {
     *                log.error("Falha na criação de usuário social: Tenant padrão
     *                'public' não encontrado.");
     * 
     *                return new BusinessException(ErrorCode.TENANT_NOT_FOUND,
     *                "Tenant padrão não encontrado para social login.");
     *                });
     * 
     *                User newUser = User.builder()
     *                .nomeCompleto("Usuário Social Google")
     *                .email("social.user@gmail.com")
     *                .cpf(ValidatorUtil.generateRandomCpf()) // Usa o método
     *                estático de ValidatorUtil
     *                .telefone("999999999")
     *                .senhaHash(passwordEncoderUtil.encode(UUID.randomUUID().toString()))
     *                // Senha aleatória para
     *                // social login
     *                .userType(User.UserType.CLIENTE)
     *                .status(User.UserStatus.ACTIVE)
     *                .tenant(defaultTenant)
     *                .build();
     *                // Adicionar role CLIENTE
     *                Role clienteRole =
     *                roleRepository.findByNome(Roles.ROLE_CLIENT)
     *                .orElseThrow(() -> {
     *                log.error("Falha na criação de usuário social: Role CLIENTE
     *                não encontrada.");
     * 
     *                return new BusinessException(ErrorCode.DATABASE_ERROR,
     *                "Role CLIENTE não encontrada.");
     *                });
     *                newUser.getRoles().add(clienteRole);
     *                return userRepository.save(newUser);
     *                });
     * 
     *                String accessToken = jwtUtil.generateToken(new
     *                CustomUserDetails(user));
     *                RefreshToken refreshToken =
     *                refreshTokenService.createRefreshToken(user);
     *                log.info("Login social Google bem-sucedido para o usuário:
     *                {}", user.getEmail());
     *                return AuthResponse.builder()
     *                .accessToken(accessToken)
     *                .refreshToken(refreshToken.getToken())
     *                .build();
     * 
     *                } else if ("LINKEDIN".equalsIgnoreCase(request.getProvider()))
     *                {
     *                log.debug("Processando login social com LinkedIn.");
     *                // Simula validação do token LinkedIn
     *                if (!"valid-linkedin-token-456".equals(request.getToken())) {
     *                log.warn("Falha no login social LinkedIn: Token inválido.");
     * 
     *                throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token
     *                LinkedIn inválido.");
     *                }
     *                // Simula busca ou criação de usuário
     *                User user =
     *                userRepository.findByEmail("social.user@linkedin.com").orElseGet(()
     *                -> {
     *                log.info("Criando novo usuário para login social LinkedIn:
     *                social.user@linkedin.com");
     *                // Para social login, você precisará de um tenant padrão para
     *                associar o usuário
     *                Tenant defaultTenant =
     *                tenantRepository.findBySchemaName("public")
     *                .orElseThrow(() -> {
     *                log.error("Falha na criação de usuário social: Tenant padrão
     *                'public' não encontrado.");
     * 
     *                return new BusinessException(ErrorCode.TENANT_NOT_FOUND,
     *                "Tenant padrão não encontrado para social login.");
     *                });
     * 
     *                User newUser = User.builder()
     *                .nomeCompleto("Usuário Social LinkedIn")
     *                .email("social.user@linkedin.com")
     *                .cpf(ValidatorUtil.generateRandomCpf()) // Usa o método
     *                estático de ValidatorUtil
     *                .telefone("888888888")
     *                .senhaHash(passwordEncoderUtil.encode(UUID.randomUUID().toString()))
     *                // Senha aleatória para
     *                // social login
     *                .userType(User.UserType.CLIENTE)
     *                .status(User.UserStatus.ACTIVE)
     *                .tenant(defaultTenant)
     *                .build();
     *                // Adicionar role CLIENTE
     *                Role clienteRole =
     *                roleRepository.findByNome(Roles.ROLE_CLIENT)
     *                .orElseThrow(() -> {
     *                log.error("Falha na criação de usuário social: Role CLIENTE
     *                não encontrada.");
     * 
     *                return new BusinessException(ErrorCode.USER_NOT_FOUND,
     *                "Role CLIENTE não encontrada.");
     *                });
     *                newUser.getRoles().add(clienteRole);
     *                return userRepository.save(newUser);
     *                });
     * 
     *                String accessToken = jwtUtil.generateToken(new
     *                CustomUserDetails(user));
     *                RefreshToken refreshToken =
     *                refreshTokenService.createRefreshToken(user);
     *                log.info("Login social LinkedIn bem-sucedido para o usuário:
     *                {}", user.getEmail());
     *                return AuthResponse.builder()
     *                .accessToken(accessToken)
     *                .refreshToken(refreshToken.getToken())
     *                .build();
     *                } else {
     *                log.warn("Provedor social não suportado: {}",
     *                request.getProvider());
     * 
     *                throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE,
     *                "Provedor social não suportado.");
     *                }
     *                }
     */
}
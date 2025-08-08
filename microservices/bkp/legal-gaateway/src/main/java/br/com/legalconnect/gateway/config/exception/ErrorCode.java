package br.com.legalconnect.gateway.config.exception;

import lombok.Getter;

/**
 * @enum ErrorCode
 * @brief Enumeração que define códigos de erro padronizados para a aplicação.
 *
 *        Cada código de erro possui uma mensagem associada, facilitando a
 *        padronização
 *        de respostas de erro na API e a internacionalização ou localização
 *        futura.
 */
@Getter
public enum ErrorCode {
    // Erros gerais de validação
    DADOS_INVALIDOS("001", "Dados fornecidos são inválidos."),
    REQUISICAO_MAL_FORMADA("002", "A requisição está mal formada."),
    PARAMETRO_AUSENTE("003", "Parâmetro obrigatório ausente."),

    // Erros de entidade / negócio
    ENTIDADE_NAO_ENCONTRADA("100", "Entidade não encontrada."),
    EMPRESA_NAO_ENCONTRADA("101", "Empresa não encontrada."),
    PROFISSIONAL_NAO_ENCONTRADO("102", "Profissional não encontrado."),
    CLIENTE_NAO_ENCONTRADO("103", "Cliente não encontrado."),
    ADMINISTRADOR_NAO_ENCONTRADO("104", "Administrador não encontrado."),
    PLANO_NAO_ENCONTRADO("105", "Plano não encontrado."),
    USER_NAO_ENCONTRADO("106", "Usuário associado não encontrado."),

    // Erros de duplicidade
    CNPJ_DUPLICADO("200", "CNPJ já cadastrado."),
    CPF_DUPLICADO("201", "CPF já cadastrado."),
    OAB_DUPLICADA("202", "Número da OAB já cadastrado."),
    EMAIL_DUPLICADO("203", "Email já cadastrado."),
    NOME_PLANO_DUPLICADO("204", "Nome do plano já cadastrado."),

    // Erros de integridade
    INTEGRIDADE_VIOLADA("300", "Violação de integridade de dados."),
    RECURSO_EM_USO("301", "Recurso não pode ser excluído pois está em uso."),

    // Erros de sistema / internos
    ERRO_INTERNO_SERVIDOR("500", "Ocorreu um erro interno no servidor."),
    SERVICO_INDISPONIVEL("503", "Serviço temporariamente indisponível."),

    // Erros Gerais/Comuns (1000-1999)
    GENERIC_ERROR("1000", "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."),
    INVALID_INPUT("1001", "Dados de entrada inválidos."),
    RESOURCE_NOT_FOUND("1002", "Recurso não encontrado."),
    UNAUTHORIZED_ACCESS("1003", "Acesso não autorizado. Credenciais inválidas ou ausentes."),
    FORBIDDEN_ACCESS("1004", "Acesso negado. Você não tem permissão para realizar esta ação."),
    SERVICE_UNAVAILABLE("1005", "O serviço está temporariamente indisponível. Tente novamente mais tarde."),
    TOO_MANY_REQUESTS("1006", "Muitas requisições. Por favor, aguarde e tente novamente."),
    VALIDATION_ERROR("1007", "Erro de validação nos dados fornecidos."),
    DATABASE_ERROR("1008", "Erro ao acessar o banco de dados."),
    INTEGRATION_ERROR("1009", "Erro de integração com serviço externo."),

    // Erros de Autenticação e Autorização (2000-2999)
    INVALID_CREDENTIALS("2000", "Credenciais de autenticação inválidas."),
    ACCOUNT_LOCKED("2001", "Sua conta está bloqueada."),
    ACCOUNT_DISABLED("2002", "Sua conta está desativada."),
    TOKEN_EXPIRED("2003", "O token de acesso expirou."),
    INVALID_TOKEN("2004", "O token de acesso é inválido."),
    REFRESH_TOKEN_EXPIRED("2005", "O refresh token expirou. Faça login novamente."),
    INVALID_REFRESH_TOKEN("2006", "O refresh token é inválido."),
    USER_NOT_FOUND("2007", "Usuário não encontrado."),
    EMAIL_ALREADY_REGISTERED("2008", "Este e-mail já está cadastrado."),
    PASSWORD_RESET_FAILED("2009", "Falha ao redefinir a senha."),
    INVALID_CURRENT_PASSWORD("2010", "A senha atual fornecida está incorreta."), // Novo erro
    PASSWORD_RESET_TOKEN_INVALID("2011", "Token de redefinição de senha inválido."), // Novo erro
    PASSWORD_RESET_TOKEN_EXPIRED("2012", "Token de redefinição de senha expirado."), // Novo erro
    PASSWORD_RESET_TOKEN_USED("2013", "Token de redefinição de senha já utilizado."), // Novo erro
    PASSWORD_RESET_TOKEN_EXCEEDED("2014", "Limite de redefinições de senha excedido."), // Novo erro

    // Erros de Negócio Específicos (3000-3999) - Exemplo
    TENANT_NOT_FOUND("3000", "Tenant não encontrado."),
    TENANT_DISABLED("3001", "O tenant está desativado."),
    SUBSCRIPTION_EXPIRED("3002", "Sua assinatura expirou."),
    PLAN_NOT_ACTIVE("3003", "O plano selecionado não está ativo."),
    SERVICE_LIMIT_EXCEEDED("3004", "Limite de serviços agendáveis excedido para o seu plano."),
    ADVOCATE_NOT_AVAILABLE("3005", "Advogado não disponível no horário selecionado."),
    APPOINTMENT_CONFLICT("3006", "Conflito de agendamento. O horário já está ocupado."),
    PAYMENT_FAILED("3007", "Falha no processamento do pagamento."),
    INVALID_PROMO_CODE("3008", "Código promocional inválido ou expirado."),
    DOCUMENT_UPLOAD_FAILED("3009", "Falha ao fazer upload do documento."),
    INVALID_DOCUMENT_FORMAT("3010", "Formato de documento inválido."),

    // Erros de Validação de Campo (4000-4999) - Mais específicos, geralmente
    // tratados por @Valid
    FIELD_REQUIRED("4000", "O campo '%s' é obrigatório."),
    INVALID_FORMAT("4001", "O campo '%s' possui formato inválido."),
    MIN_LENGTH("4002", "O campo '%s' deve ter no mínimo %d caracteres."),
    MAX_LENGTH("4003", "O campo '%s' deve ter no máximo %d caracteres."),
    INVALID_EMAIL("4004", "O e-mail fornecido é inválido."),
    INVALID_CPF("4005", "O CPF fornecido é inválido."),
    INVALID_CNPJ("4006", "O CNPJ fornecido é inválido."),
    PASSWORD_TOO_WEAK("4007", "A senha é muito fraca."),
    DATE_IN_PAST("4008", "A data não pode ser no passado."),
    INVALID_ENUM_VALUE("4009", "Valor inválido para o campo '%s'.");

    private final String code;
    private final String message;

    /**
     * Construtor para ErrorCode.
     *
     * @param code    O código único do erro.
     * @param message A mensagem descritiva do erro.
     */
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @brief Retorna a mensagem de erro formatada com argumentos.
     * @param args Argumentos para formatar a mensagem.
     * @return A mensagem de erro formatada.
     */
    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }
}

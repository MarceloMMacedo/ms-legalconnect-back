package br.com.legalconnect.common.exception;

/**
 * @class Roles
 * @brief Classe de constantes para os papéis (roles) de usuário na aplicação.
 *
 *        Define os nomes dos papéis como constantes estáticas para evitar erros
 *        de digitação
 *        e centralizar a gestão dos papéis utilizados na segurança da
 *        aplicação.
 */
public final class Roles {

    // Papéis de Usuário
    public static final String ROLE_ADMIN = "ROLE_ADMIN"; // Administrador do sistema
    public static final String ROLE_TENANT_ADMIN = "ROLE_TENANT_ADMIN"; // Administrador de um tenant específico
    public static final String ROLE_ADVOCATE = "ROLE_ADVOCATE"; // Advogado
    public static final String ROLE_CLIENT = "ROLE_CLIENT"; // Cliente final
    public static final String ROLE_USER = "ROLE_USER"; // Usuário genérico (pode ser usado para usuários logados sem um
                                                        // papel específico mais alto)

    // Prefixos e Sufixos (se necessário, para Spring Security, por exemplo)
    public static final String PREFIX = "ROLE_"; // Prefixo padrão para papéis no Spring Security

    /**
     * Construtor privado para evitar instanciação.
     */
    private Roles() {
        // Construtor privado para garantir que a classe não seja instanciada.
        // É uma classe de utilidade com apenas constantes estáticas.
    }

    /**
     * @brief Converte um nome de papel para o formato esperado pelo Spring Security
     *        (com prefixo "ROLE_").
     * @param roleName O nome do papel sem o prefixo.
     * @return O nome do papel com o prefixo "ROLE_".
     */
    public static String withPrefix(String roleName) {
        if (roleName != null && !roleName.startsWith(PREFIX)) {
            return PREFIX + roleName;
        }
        return roleName;
    }

    /**
     * @brief Remove o prefixo "ROLE_" de um nome de papel.
     * @param roleName O nome do papel com ou sem o prefixo.
     * @return O nome do papel sem o prefixo "ROLE_".
     */
    public static String withoutPrefix(String roleName) {
        if (roleName != null && roleName.startsWith(PREFIX)) {
            return roleName.substring(PREFIX.length());
        }
        return roleName;
    }
}
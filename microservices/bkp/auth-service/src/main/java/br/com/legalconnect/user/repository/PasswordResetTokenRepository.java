package br.com.legalconnect.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.user.entity.PasswordResetToken;
import br.com.legalconnect.user.entity.User; // Importar User

/**
 * @interface PasswordResetTokenRepository
 * @brief Repositório JPA para a entidade PasswordResetToken.
 *
 *        Fornece operações CRUD e métodos de consulta personalizados para
 *        gerenciar
 *        os tokens de redefinição de senha.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    /**
     * @brief Busca um PasswordResetToken pelo seu valor de token.
     * @param token O valor String do token de redefinição.
     * @return Um Optional contendo o PasswordResetToken, se encontrado.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * @brief Busca um PasswordResetToken associado a um usuário específico.
     * @param user O objeto User para o qual o token é procurado.
     * @return Um Optional contendo o PasswordResetToken, se encontrado.
     */
    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByUserAndUsado(User user, boolean b);

    Optional<PasswordResetToken> findFirstByUserId(UUID id);

}
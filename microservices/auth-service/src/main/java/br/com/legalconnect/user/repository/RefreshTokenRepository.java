package br.com.legalconnect.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.user.entity.RefreshToken;
import br.com.legalconnect.user.entity.User;

/**
 * @interface RefreshTokenRepository
 * @brief Repositório JPA para a entidade RefreshToken.
 *
 *        Fornece operações CRUD (Create, Read, Update, Delete) e métodos de
 *        consulta personalizados
 *        para gerenciar os tokens de atualização de sessão dos usuários.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * @brief Busca um RefreshToken pelo seu valor de token.
     * @param token O valor String do refresh token.
     * @return Um Optional contendo o RefreshToken, se encontrado.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * @brief Busca um RefreshToken associado a um usuário específico.
     * @param user O objeto User para o qual o refresh token é procurado.
     * @return Um Optional contendo o RefreshToken, se encontrado.
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * @brief Deleta um RefreshToken pelo seu valor de token.
     * @param token O valor String do refresh token a ser deletado.
     */
    void deleteByToken(String token);
}
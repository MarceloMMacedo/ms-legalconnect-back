package br.com.legalconnect.user.entity;

import java.time.Instant;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class RefreshToken
 * @brief Entidade que representa o Refresh Token no banco de dados.
 *
 *        Esta tabela de refresh tokens agora reside nos schemas de tenant.
 */
@Entity
@Table(name = "tb_refresh_token") // A tabela tb_refresh_token agora reside no schema do tenant
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY) // Relacionamento um-para-um com User
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Coluna de chave estrangeira
    private User user; // Usuário ao qual este refresh token está associado

    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token; // O valor real do refresh token

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm; // Data e hora em que este refresh token se tornará inválido
}
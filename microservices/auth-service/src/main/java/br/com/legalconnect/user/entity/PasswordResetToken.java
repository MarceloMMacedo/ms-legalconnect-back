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
 * @class PasswordResetToken
 * @brief Entidade que representa um token de redefinição de senha no banco de
 *        dados.
 *
 *        Este token é gerado quando um usuário solicita a recuperação de senha
 *        e é usado
 *        para validar a solicitação antes de permitir a alteração da senha.
 */
@Entity
@Table(name = "tb_password_reset_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PasswordResetToken extends BaseEntity {

    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token; // O valor real do token de redefinição

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user; // Usuário ao qual este token está associado

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm; // Data e hora em que este token se tornará inválido

    @Column(name = "usado", nullable = false)
    private boolean usado; // Indica se o token já foi usado
}
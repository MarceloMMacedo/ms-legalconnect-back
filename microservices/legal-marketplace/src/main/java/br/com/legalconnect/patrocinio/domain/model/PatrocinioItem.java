//
// Entidade base abstrata JPA para representar um item de patrocínio.
// Usa herança de tabela única (@Inheritance) para persistir todos os tipos de patrocínio em uma única tabela.
//
package br.com.legalconnect.patrocinio.domain.model;

import br.com.legalconnect.common.dto.BaseEntity;
import br.com.legalconnect.patrocinio.domain.enums.PatrocinioStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Entidade JPA base abstrata para itens de patrocínio.
 * Usa herança de tabela única (@Inheritance) para centralizar todos os patrocínios em uma única tabela.
 * O campo 'tipo' atua como o discriminador.
 */
@Entity
@Table(name = "tb_patrocinio")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class PatrocinioItem extends BaseEntity {

    @Column(name = "tipo", insertable = false, updatable = false)
    protected String tipo;

    @Column(nullable = false, length = 255)
    protected String link;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected PatrocinioStatus status;
}
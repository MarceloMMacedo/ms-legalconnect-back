package br.com.legalconnect.user.entity;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Role
 * @brief Entidade para definir os papéis de acesso do usuário no sistema.
 *
 * Esta tabela de roles agora reside nos schemas de tenant, o que
 * significa
 * que cada tenant pode ter seus próprios conjuntos de roles ou roles com
 * IDs diferentes.
 */
@Entity
@Table(name = "tb_role") // A tabela tb_role agora reside no schema do tenant
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome; // Nome descritivo e único do papel (ex: CLIENTE, ADVOGADO)

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao; // Uma descrição detalhada do papel
}
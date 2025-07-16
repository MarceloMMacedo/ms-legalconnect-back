package br.com.legalconnect.common.dto;
// common/BaseEntity.java

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class BaseEntity
 * @brief Classe base para entidades persistentes, com campos comuns como
 *        createdAt, updatedAt.
 *
 *        Anotada com `@MappedSuperclass`, suas propriedades são herdadas por
 *        entidades que a estendem,
 *        mas ela própria não é uma entidade mapeada para uma tabela. Contém o
 *        ID e campos de auditoria.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass // Indica que esta classe é uma superclasse mapeada para outras entidades
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gera UUIDs para os IDs
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp // Preenche automaticamente a data de criação
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt; // Carimbo de data/hora (com fuso horário) exato em que o registro foi criado

    @UpdateTimestamp // Preenche automaticamente a data de atualização
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // Carimbo de data/hora (com fuso horário) da última vez que o registro foi
                               // modificado
}

package br.com.legalconnect.common.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @class BaseEntity
 * @brief Classe base abstrata para todas as entidades persistentes.
 * Fornece campos comuns como ID, data de criação e data de atualização.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass // Indica que esta classe é uma superclasse mapeada para outras entidades
public abstract class BaseEntity implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(BaseEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // Identificador único da entidade

    protected LocalDateTime dataCriacao; // Data e hora de criação do registro

    protected LocalDateTime dataAtualizacao; // Data e hora da última atualização do registro

    /**
     * Define a data de criação antes de persistir a entidade.
     */
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        log.debug("Criando entidade: {}. Data de criação: {}", this.getClass().getSimpleName(), dataCriacao);
    }

    /**
     * Atualiza a data de atualização antes de atualizar a entidade.
     */
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        log.debug("Atualizando entidade: {}. ID: {}. Data de atualização: {}", this.getClass().getSimpleName(), id, dataAtualizacao);
    }
}
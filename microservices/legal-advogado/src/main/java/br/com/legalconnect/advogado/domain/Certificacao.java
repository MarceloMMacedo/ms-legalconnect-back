package br.com.legalconnect.advogado.domain;

import java.time.LocalDate;
import java.util.UUID;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_certificacao_profissional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Certificacao extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "instituicao", length = 255)
    private String instituicao;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional; // Referência à entidade JPA ProfissionalEntity

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}
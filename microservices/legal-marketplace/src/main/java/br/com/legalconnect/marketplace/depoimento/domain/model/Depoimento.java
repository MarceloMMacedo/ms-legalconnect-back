package br.com.legalconnect.marketplace.depoimento.domain.model;

import java.util.UUID;

import br.com.legalconnect.common.dto.BaseEntity;
import br.com.legalconnect.marketplace.depoimento.domain.enums.DepoimentoStatus; // Importado o novo enum
import br.com.legalconnect.marketplace.depoimento.domain.enums.TipoDepoimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA para representar um depoimento.
 * Inclui associação com o usuário, o tipo de depoimento e seu status.
 */
@Entity
@Table(name = "tb_depoimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Depoimento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Geração automática de UUID pelo JPA/Hibernate
    private UUID id;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(nullable = false, length = 100)
    private String nome; // Nome da pessoa que deu o depoimento

    @Column(length = 100)
    private String local; // Cidade/Estado ou empresa

    @Column(name = "foto_url", length = 255)
    private String fotoUrl; // URL da foto do depoente

    @Column(name = "user_id", nullable = false)
    private UUID userId; // ID do usuário (cliente ou profissional) relacionado ao depoimento

    @Enumerated(EnumType.STRING) // Armazena o enum como String no banco de dados
    @Column(name = "tipo_depoimento", nullable = false, length = 20)
    private TipoDepoimento tipoDepoimento; // Tipo: CLIENTE ou PROFISSIONAL

    @Enumerated(EnumType.STRING) // Armazena o enum como String no banco de dados
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default // Define um valor padrão para o Builder
    private DepoimentoStatus status = DepoimentoStatus.PENDENTE; // Status inicial PENDENTE
}
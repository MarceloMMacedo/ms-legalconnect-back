package br.com.legalconnect.advogado.domain.modal.entity;

import java.util.UUID;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_role_profissional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoleProfissionalEntity extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
}

// --- Entidades de Dados Mestres (se gerenciadas localmente no Professional
// Service) ---
// NOTA: Se estas entidades residirem em um "Master Data Service" separado e o
// Professional Service
// apenas referenciar seus IDs, então estas classes de entidade não estariam
// aqui.
// Elas são incluídas assumindo que o Professional Service pode ter uma cópia
// local ou gerenciar
// essas entidades para seu próprio contexto.
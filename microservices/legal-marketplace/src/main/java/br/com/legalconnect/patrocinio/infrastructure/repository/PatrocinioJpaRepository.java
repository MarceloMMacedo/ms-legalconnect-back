//
// Repositório JPA genérico para a entidade base PatrocinioItem.
//
package br.com.legalconnect.patrocinio.infrastructure.repository;

import br.com.legalconnect.patrocinio.domain.enums.PatrocinioStatus;
import br.com.legalconnect.patrocinio.domain.model.PatrocinioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para a entidade base PatrocinioItem.
 * Através do polimorfismo, este repositório pode lidar com todos os tipos de patrocínios.
 */
@Repository
public interface PatrocinioJpaRepository extends JpaRepository<PatrocinioItem, UUID> {
    /**
     * Busca uma lista de patrocinadores pelo seu status.
     *
     * @param status O status do patrocinador (ACTIVE ou INACTIVE).
     * @return Uma lista de patrocinadores com o status especificado.
     */
    List<PatrocinioItem> findByStatus(PatrocinioStatus status);
}
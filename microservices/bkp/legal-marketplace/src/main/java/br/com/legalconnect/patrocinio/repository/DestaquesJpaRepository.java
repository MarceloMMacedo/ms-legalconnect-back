//
// Repositório JPA genérico para a entidade base PatrocinioItem.
//
package br.com.legalconnect.patrocinio.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.patrocinio.domain.DestaquesItem;
import br.com.legalconnect.patrocinio.domain.enums.PatrocinioStatus;

/**
 * Repositório Spring Data JPA para a entidade base PatrocinioItem.
 * Através do polimorfismo, este repositório pode lidar com todos os tipos de
 * patrocínios.
 */
@Repository
public interface DestaquesJpaRepository extends JpaRepository<DestaquesItem, UUID> {
    /**
     * Busca uma lista de patrocinadores pelo seu status.
     *
     * @param status O status do patrocinador (ACTIVE ou INACTIVE).
     * @return Uma lista de patrocinadores com o status especificado.
     */
    List<DestaquesItem> findByStatus(PatrocinioStatus status);
}
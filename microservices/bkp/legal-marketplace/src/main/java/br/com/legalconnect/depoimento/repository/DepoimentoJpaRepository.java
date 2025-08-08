package br.com.legalconnect.depoimento.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.depoimento.domain.enums.DepoimentoStatus;
import br.com.legalconnect.depoimento.domain.enums.TipoDepoimento;
import br.com.legalconnect.depoimento.domain.model.Depoimento;

/**
 * Repositório JPA para a entidade Depoimento, usando Spring Data JPA.
 * Define métodos para operações CRUD e consultas personalizadas.
 */
@Repository
public interface DepoimentoJpaRepository extends JpaRepository<Depoimento, UUID> {

    /**
     * Busca uma lista de depoimentos aleatórios, limitada pela quantidade
     * especificada, apenas os APROVADOS.
     * 
     * @param limite O número máximo de depoimentos a serem retornados.
     * @return Uma lista de depoimentos aleatórios.
     */
    @Query(value = "SELECT * FROM tb_depoimento WHERE status = 'APROVADO' ORDER BY random() LIMIT :limite", nativeQuery = true)
    List<Depoimento> buscarAleatoriosAprovados(@Param("limite") int limite);

    /**
     * Busca os 5 depoimentos APROVADOS mais recentes, ordenados pela data de
     * criação em ordem decrescente.
     * 
     * @param status O status do depoimento (APROVADO).
     * @return Uma lista dos 5 depoimentos mais recentes.
     */
    List<Depoimento> findTop5ByStatusOrderByCreatedAtDesc(DepoimentoStatus status);

    /**
     * Busca depoimentos por tipo de depoimento.
     * 
     * @param tipoDepoimento O tipo de depoimento (CLIENTE ou PROFISSIONAL).
     * @return Uma lista de depoimentos do tipo especificado.
     */
    List<Depoimento> findByTipoDepoimento(TipoDepoimento tipoDepoimento);

    /**
     * Busca depoimentos relacionados a um ID de usuário específico.
     * 
     * @param userId O ID do usuário.
     * @return Uma lista de depoimentos associados ao usuário.
     */
    List<Depoimento> findByUserId(UUID userId);

    /**
     * Busca depoimentos por status.
     * 
     * @param status O status do depoimento.
     * @return Uma lista de depoimentos com o status especificado.
     */
    List<Depoimento> findByStatus(DepoimentoStatus status);
}
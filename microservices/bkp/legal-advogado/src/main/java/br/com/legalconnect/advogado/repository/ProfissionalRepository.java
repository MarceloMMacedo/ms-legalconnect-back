package br.com.legalconnect.advogado.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.Profissional;

/**
 * Repositório para a entidade {@link Profissional}.
 * Gerencia operações de persistência para os profissionais (advogados).
 */
@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, UUID> {

    /**
     * Busca um Profissional pelo número da OAB.
     *
     * @param numeroOab O número da OAB do profissional.
     * @return Um Optional contendo o Profissional, se encontrado.
     */
    Optional<Profissional> findByNumeroOab(String numeroOab);

    /**
     * Busca todos os Profissionais associados a um determinado tenant.
     *
     * @param tenantId O ID do tenant.
     * @return Uma lista de Profissionais.
     */
    Page<Profissional> findAllByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Verifica se um Profissional existe pelo número da OAB.
     * Regra de Negócio: Garante a unicidade do número da OAB.
     *
     * @param numeroOab O número da OAB a ser verificado.
     * @return true se um Profissional com a OAB já existe, false caso contrário.
     */
    boolean existsByNumeroOab(String numeroOab);

    /**
     * Verifica se um Profissional existe pelo ID da pessoa associada.
     *
     * @param pessoaId O ID da pessoa.
     * @return true se um Profissional com o ID da pessoa existe, false caso
     *         contrário.
     */
    boolean existsByPessoaId(UUID pessoaId);

    /**
     * Busca todos os estados e cidades (municípios) distintos
     * de todas as Pessoas cadastradas.
     * Retorna uma lista de arrays de objetos, onde cada array contém [estado,
     * cidade].
     *
     * @return Uma lista de Object[] contendo pares [estado, cidade].
     */
    @Query(value = "SELECT DISTINCT e.estado, e.cidade FROM tb_endereco e WHERE e.estado IS NOT NULL AND e.cidade IS NOT NULL ORDER BY e.estado, e.cidade", nativeQuery = true)
    List<Object[]> findDistinctEstadosAndCidades();

    /**
     * NOVO MÉTODO: Busca uma página de Profissionais ativos que usam o marketplace,
     * ordenando aleatoriamente para seleção inicial.
     *
     * @param pageable Objeto Pageable contendo informações de paginação (para
     *                 limitar o número de resultados aleatórios).
     * @return Uma página de Profissionais.
     */
    @Query(value = "SELECT p FROM Profissional p JOIN FETCH p.usuario u LEFT JOIN FETCH p.enderecos e LEFT JOIN FETCH p.plano pl "
            +
            "WHERE p.usaMarketplace = true AND p.statusProfissional = 'ATIVO' " +
            "ORDER BY RANDOM()") // Ordena aleatoriamente para pegar uma amostra
    Page<Profissional> findAllPublicMarketplaceProfissionais(Pageable pageable);
}
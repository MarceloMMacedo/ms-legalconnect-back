package br.com.legalconnect.advogado.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.advogado.domain.Idioma;

/**
 * Repositório para a entidade {@link Idioma}.
 * Gerencia operações de persistência para os idiomas disponíveis.
 */
@Repository
public interface IdiomaRepository extends JpaRepository<Idioma, UUID> {

    /**
     * Busca um Idioma pelo nome.
     *
     * @param nome O nome do idioma.
     * @return Um Optional contendo o Idioma, se encontrado.
     */
    Optional<Idioma> findByNome(String nome);

    /**
     * Busca todos os Idiomas por uma lista de IDs.
     * 
     * @param ids Lista de IDs dos idiomas.
     * @return Uma lista de Idiomas.
     */
    List<Idioma> findAllByIdIn(List<UUID> ids);
}
package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Profissional;

/**
 * @interface RepositorioProfissional
 * @brief Repositório Spring Data JPA para a entidade Profissional.
 *        Fornece métodos CRUD e de busca personalizados para Profissional.
 */
@Repository
public interface RepositorioProfissional extends JpaRepository<Profissional, UUID> {

    /**
     * @brief Busca um Profissional pelo CPF.
     * @param cpf O CPF do profissional.
     * @return Um Optional contendo o Profissional, se encontrado.
     */
    Optional<Profissional> findByCpf(String cpf);

    /**
     * @brief Busca um Profissional pelo número da OAB.
     * @param numeroOab O número da OAB do profissional.
     * @return Um Optional contendo o Profissional, se encontrado.
     */
    Optional<Profissional> findByNumeroOab(String numeroOab);

    /**
     * @brief Lista todos os Profissionais com paginação.
     * @param pageable Objeto Pageable para configuração de paginação.
     * @return Uma página de Profissionais.
     */
    Page<Profissional> findAll(Pageable pageable);
}
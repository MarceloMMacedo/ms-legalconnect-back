package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Cliente;

/**
 * @interface RepositorioCliente
 * @brief Repositório Spring Data JPA para a entidade Cliente.
 *        Fornece métodos CRUD e de busca personalizados para Cliente.
 */
@Repository
public interface RepositorioCliente extends JpaRepository<Cliente, UUID> {

    /**
     * @brief Busca um Cliente pelo CPF.
     * @param cpf O CPF do cliente.
     * @return Um Optional contendo o Cliente, se encontrado.
     */
    Optional<Cliente> findByCpf(String cpf);

    /**
     * @brief Lista todos os Clientes com paginação.
     * @param pageable Objeto Pageable para configuração de paginação.
     * @return Uma página de Clientes.
     */
    Page<Cliente> findAll(Pageable pageable);
}
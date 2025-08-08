package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Empresa;

/**
 * @interface RepositorioEmpresa
 * @brief Repositório Spring Data JPA para a entidade Empresa.
 *        Fornece métodos CRUD e de busca personalizados para Empresa.
 */
@Repository
public interface RepositorioEmpresa extends JpaRepository<Empresa, UUID> {

    /**
     * @brief Busca uma Empresa pelo CNPJ.
     * @param cnpj O CNPJ da empresa.
     * @return Um Optional contendo a Empresa, se encontrada.
     */
    Optional<Empresa> findByCnpj(String cnpj);

    /**
     * @brief Lista todas as Empresas com paginação.
     * @param pageable Objeto Pageable para configuração de paginação.
     * @return Uma página de Empresas.
     */
    Page<Empresa> findAll(Pageable pageable);
}
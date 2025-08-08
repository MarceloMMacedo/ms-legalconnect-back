package br.com.legalconnect.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Endereco;

/**
 * @interface RepositorioEndereco
 * @brief Repositório Spring Data JPA para a entidade Endereco.
 *        Fornece métodos CRUD básicos para Endereco.
 */
@Repository
public interface RepositorioEndereco extends JpaRepository<Endereco, UUID> {
    // Métodos de busca adicionais podem ser definidos aqui se necessário,
    // como findByPessoaId(UUID pessoaId) ou findByEmpresaId(UUID empresaId)
}
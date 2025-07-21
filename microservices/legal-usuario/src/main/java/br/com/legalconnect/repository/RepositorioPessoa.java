package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Pessoa;

/**
 * @interface RepositorioPessoa
 * @brief Repositório Spring Data JPA para a entidade Pessoa (base).
 * Fornece métodos CRUD básicos para Pessoa e busca por CPF.
 * Como Pessoa é uma entidade base abstrata com estratégia JOINED, este
 * repositório
 * pode ser usado para operações polimórficas se necessário, mas
 * geralmente
 * repositórios específicos para subclasses (Profissional, Cliente,
 * Administrador)
 * são mais comuns para operações de negócio.
 */
@Repository
public interface RepositorioPessoa extends JpaRepository<Pessoa, UUID> {

    /**
     * @brief Busca uma Pessoa pelo CPF.
     * @param cpf O CPF da pessoa.
     * @return Um Optional contendo a Pessoa, se encontrada.
     */
    Optional<Pessoa> findByCpf(String cpf);
}
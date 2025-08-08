package br.com.legalconnect.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.legalconnect.entity.Plano;

/**
 * @interface RepositorioPlano
 * @brief Repositório Spring Data JPA para a entidade Plano.
 *        Fornece métodos CRUD e de busca personalizados para Plano.
 *        **Nota:** Em uma arquitetura de microsserviços ideal, esta entidade e
 *        seu
 *        repositório poderiam residir em um serviço de 'marketplace' ou
 *        'assinatura'.
 *        Aqui, é mantido para fins de completude do modelo de dados do
 *        user-service,
 *        mas a gestão real dos planos pode ser externa.
 */
@Repository
public interface RepositorioPlano extends JpaRepository<Plano, UUID> {

    /**
     * @brief Busca um Plano pelo nome.
     * @param nome O nome do plano.
     * @return Um Optional contendo o Plano, se encontrado.
     */
    Optional<Plano> findByNome(String nome);
}
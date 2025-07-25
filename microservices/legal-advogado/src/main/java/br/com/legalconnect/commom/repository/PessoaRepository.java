package br.com.legalconnect.commom.repository;

import br.com.legalconnect.commom.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para a entidade {@link Pessoa}.
 * Gerencia operações de persistência para informações de pessoas no sistema.
 */
@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, UUID> {

    /**
     * Verifica se existe uma Pessoa com o CPF fornecido.
     * Regra de Negócio: Garante a unicidade do CPF no sistema.
     *
     * @param cpf O CPF a ser verificado.
     * @return true se uma Pessoa com o CPF já existe, false caso contrário.
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca uma Pessoa pelo ID do usuário associado.
     *
     * @param userId O ID do usuário.
     * @return Um Optional contendo a Pessoa, se encontrada.
     */
    Optional<Pessoa> findByUsuarioId(UUID userId);

    /**
     * Busca uma Pessoa pelo CPF.
     *
     * @param cpf O CPF da Pessoa.
     * @return Um Optional contendo a Pessoa, se encontrada.
     */
    Optional<Pessoa> findByCpf(String cpf);
}
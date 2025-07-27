package br.com.legalconnect.depoimento.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import br.com.legalconnect.depoimento.domain.enums.DepoimentoStatus;
import br.com.legalconnect.depoimento.domain.enums.TipoDepoimento;
import br.com.legalconnect.depoimento.domain.model.Depoimento;
import br.com.legalconnect.depoimento.infrastructure.persistence.DepoimentoJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de domínio para gerenciar operações relacionadas a Depoimentos.
 * Contém a lógica de negócio principal e interage diretamente com o
 * repositório.
 * Este serviço deve focar nas operações sobre o agregado de Depoimento, não em
 * casos de uso específicos.
 */
@Service
@RequiredArgsConstructor
public class DepoimentoService {

    private final DepoimentoJpaRepository repository;

    /**
     * Salva um novo depoimento.
     * 
     * @param novoDepoimento A entidade Depoimento a ser salva.
     * @return O depoimento salvo.
     */
    public Depoimento salvar(Depoimento novoDepoimento) {
        return repository.save(novoDepoimento);
    }

    /**
     * Atualiza um depoimento existente.
     * 
     * @param id                   O ID do depoimento a ser atualizado.
     * @param depoimentoAtualizado A entidade Depoimento com os dados atualizados.
     * @return O depoimento atualizado.
     */
    public Optional<Depoimento> atualizar(UUID id, Depoimento depoimentoAtualizado) {
        return repository.findById(id).map(depoimento -> {
            depoimento.setTexto(depoimentoAtualizado.getTexto());
            depoimento.setNome(depoimentoAtualizado.getNome());
            depoimento.setLocal(depoimentoAtualizado.getLocal());
            depoimento.setFotoUrl(depoimentoAtualizado.getFotoUrl());
            depoimento.setUserId(depoimentoAtualizado.getUserId());
            depoimento.setTipoDepoimento(depoimentoAtualizado.getTipoDepoimento());
            depoimento.setStatus(depoimentoAtualizado.getStatus()); // Atualiza o status
            return repository.save(depoimento);
        });
    }

    /**
     * Altera o status de um depoimento.
     * 
     * @param id         O ID do depoimento.
     * @param novoStatus O novo status a ser atribuído.
     * @return Um Optional contendo o depoimento atualizado, se encontrado.
     */
    public Optional<Depoimento> alterarStatus(UUID id, DepoimentoStatus novoStatus) {
        return repository.findById(id).map(depoimento -> {
            depoimento.setStatus(novoStatus);
            return repository.save(depoimento);
        });
    }

    /**
     * Exclui um depoimento pelo seu ID.
     * 
     * @param id O ID do depoimento a ser excluído.
     */
    public void excluir(UUID id) {
        repository.deleteById(id);
    }

    /**
     * Busca um depoimento pelo seu ID.
     * 
     * @param id O ID do depoimento.
     * @return Um Optional contendo o depoimento, se encontrado.
     */
    public Optional<Depoimento> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    /**
     * Lista todos os depoimentos.
     * 
     * @return Uma lista de todos os depoimentos.
     */
    public List<Depoimento> listarTodos() {
        return repository.findAll();
    }

    /**
     * Lista depoimentos por tipo.
     * 
     * @param tipo O tipo de depoimento (CLIENTE ou PROFISSIONAL).
     * @return Uma lista de depoimentos do tipo especificado.
     */
    public List<Depoimento> listarPorTipo(TipoDepoimento tipo) {
        return repository.findByTipoDepoimento(tipo);
    }

    /**
     * Lista depoimentos por ID de usuário.
     * 
     * @param userId O ID do usuário.
     * @return Uma lista de depoimentos associados ao usuário.
     */
    public List<Depoimento> listarPorUserId(UUID userId) {
        return repository.findByUserId(userId);
    }
}
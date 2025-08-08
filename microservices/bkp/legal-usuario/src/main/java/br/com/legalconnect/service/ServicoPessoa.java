// br/com/legalconnect/service/ServicoPessoa.java
package br.com.legalconnect.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier; // Importar Qualifier
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.dto.PessoaResponseDTO;
import br.com.legalconnect.entity.Pessoa;
import br.com.legalconnect.mapper.PessoaMapper;
import br.com.legalconnect.repository.RepositorioPessoa;

/**
 * @class ServicoPessoa
 * @brief Serviço de domínio para operações genéricas relacionadas a Pessoas.
 *        Como Pessoa é uma entidade base abstrata, este serviço lida com
 *        operações
 *        que podem ser comuns a todas as subclasses (Profissional, Cliente,
 *        Administrador).
 *        Operações de criação/atualização específicas devem ser tratadas nos
 *        serviços das subclasses.
 */
@Service
public class ServicoPessoa {

    private final RepositorioPessoa repositorioPessoa;
    private final PessoaMapper pessoaMapper;

    @Autowired
    public ServicoPessoa(RepositorioPessoa repositorioPessoa,
            @Qualifier("pessoaMapperImpl") PessoaMapper pessoaMapper) {
        this.repositorioPessoa = repositorioPessoa;
        this.pessoaMapper = pessoaMapper;
    }

    /**
     * @brief Busca uma Pessoa pelo ID.
     * @param id ID da Pessoa a ser buscada.
     * @return DTO com os dados da Pessoa encontrada.
     * @throws BusinessException Se a Pessoa não for encontrada.
     */
    @Transactional(readOnly = true)
    public PessoaResponseDTO buscarPessoaPorId(UUID id) {
        Pessoa pessoa = repositorioPessoa.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Pessoa com ID " + id + " não encontrada."));
        return pessoaMapper.toResponseDTO(pessoa);
    }

    /**
     * @brief Lista todas as Pessoas.
     * @return Lista de DTOs de Pessoas.
     */
    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> listarTodasPessoas() {
        return repositorioPessoa.findAll().stream()
                .map(pessoaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * @brief Exclui uma Pessoa pelo ID.
     * @param id ID da Pessoa a ser excluída.
     * @throws BusinessException Se a Pessoa não for encontrada.
     *                           **Nota:** A exclusão de Pessoa deve ser feita com
     *                           cautela, pois pode
     *                           impactar subclasses (Profissional, Cliente,
     *                           Administrador) e o User associado.
     *                           Idealmente, a exclusão de subclasses deveria ser o
     *                           ponto de entrada.
     */
    @Transactional
    public void excluirPessoa(UUID id) {
        if (!repositorioPessoa.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Pessoa com ID " + id + " não encontrada para exclusão.");
        }
        repositorioPessoa.deleteById(id);
    }
}

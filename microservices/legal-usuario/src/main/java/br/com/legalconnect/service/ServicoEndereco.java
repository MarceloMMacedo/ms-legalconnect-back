package br.com.legalconnect.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.common.exception.BusinessException; // Importa da common-lib
import br.com.legalconnect.common.exception.ErrorCode; // Importa da common-lib
import br.com.legalconnect.dto.EnderecoRequestDTO;
import br.com.legalconnect.dto.EnderecoResponseDTO;
import br.com.legalconnect.entity.Endereco;
import br.com.legalconnect.mapper.EnderecoMapper;
import br.com.legalconnect.repository.RepositorioEndereco;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoEndereco
 * @brief Serviço de domínio para gerenciar operações relacionadas a Endereços.
 *        Este serviço pode ser usado para operações diretas em Endereços,
 *        embora na maioria dos casos eles sejam gerenciados em cascata por
 *        Pessoa ou Empresa.
 */
@Service
@RequiredArgsConstructor
public class ServicoEndereco {

    private final RepositorioEndereco repositorioEndereco;
    private final EnderecoMapper enderecoMapper;

    /**
     * @brief Cadastra um novo Endereco.
     * @param requestDTO DTO com os dados do Endereco a ser cadastrado.
     * @return DTO com os dados do Endereco cadastrado.
     */
    @Transactional
    public EnderecoResponseDTO cadastrarEndereco(EnderecoRequestDTO requestDTO) {
        Endereco endereco = enderecoMapper.toEntity(requestDTO);
        Endereco savedEndereco = repositorioEndereco.save(endereco);
        return enderecoMapper.toResponseDTO(savedEndereco);
    }

    /**
     * @brief Busca um Endereco pelo ID.
     * @param id ID do Endereco a ser buscado.
     * @return DTO com os dados do Endereco encontrado.
     * @throws BusinessException Se o Endereco não for encontrado.
     */
    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarEnderecoPorId(UUID id) {
        Endereco endereco = repositorioEndereco.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Endereço com ID " + id + " não encontrado."));
        return enderecoMapper.toResponseDTO(endereco);
    }

    /**
     * @brief Lista todos os Enderecos.
     * @return Lista de DTOs de Enderecos.
     */
    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listarEnderecos() {
        return repositorioEndereco.findAll().stream()
                .map(enderecoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * @brief Atualiza os dados de um Endereco existente.
     * @param id         ID do Endereco a ser atualizado.
     * @param requestDTO DTO com os dados atualizados do Endereco.
     * @return DTO com os dados do Endereco atualizado.
     * @throws BusinessException Se o Endereco não for encontrado.
     */
    @Transactional
    public EnderecoResponseDTO atualizarEndereco(UUID id, EnderecoRequestDTO requestDTO) {
        Endereco existingEndereco = repositorioEndereco.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Endereço com ID " + id + " não encontrado para atualização."));

        // Atualizar campos básicos do Endereco
        existingEndereco.setLogradouro(requestDTO.getLogradouro());
        existingEndereco.setNumero(requestDTO.getNumero());
        existingEndereco.setComplemento(requestDTO.getComplemento());
        existingEndereco.setBairro(requestDTO.getBairro());
        existingEndereco.setCidade(requestDTO.getCidade());
        existingEndereco.setEstado(requestDTO.getEstado());
        existingEndereco.setCep(requestDTO.getCep());
        existingEndereco.setPais(requestDTO.getPais());
        existingEndereco.setTipoEndereco(requestDTO.getTipoEndereco());

        Endereco updatedEndereco = repositorioEndereco.save(existingEndereco);
        return enderecoMapper.toResponseDTO(updatedEndereco);
    }

    /**
     * @brief Exclui um Endereco pelo ID.
     * @param id ID do Endereco a ser excluído.
     * @throws BusinessException Se o Endereco não for encontrado.
     */
    @Transactional
    public void excluirEndereco(UUID id) {
        if (!repositorioEndereco.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Endereço com ID " + id + " não encontrado para exclusão.");
        }
        repositorioEndereco.deleteById(id);
    }
}
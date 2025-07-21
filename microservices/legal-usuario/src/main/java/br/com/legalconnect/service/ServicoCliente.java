package br.com.legalconnect.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.common.exception.BusinessException; // Importa da common-lib
import br.com.legalconnect.common.exception.ErrorCode; // Importa da common-lib
import br.com.legalconnect.dto.ClienteRequestDTO;
import br.com.legalconnect.dto.ClienteResponseDTO;
import br.com.legalconnect.entity.Cliente;
import br.com.legalconnect.entity.Endereco;
import br.com.legalconnect.entity.User;
import br.com.legalconnect.mapper.ClienteMapper;
import br.com.legalconnect.mapper.EnderecoMapper;
import br.com.legalconnect.repository.RepositorioCliente;
import br.com.legalconnect.repository.RepositorioUser;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoCliente
 * @brief Serviço de domínio para gerenciar operações relacionadas a Clientes.
 *        Contém a lógica de negócio para criação, busca, atualização e exclusão
 *        de Clientes.
 */
@Service
@RequiredArgsConstructor
public class ServicoCliente {

    private final RepositorioCliente repositorioCliente;
    private final RepositorioUser repositorioUser;
    private final ClienteMapper clienteMapper;
    private final EnderecoMapper enderecoMapper;

    /**
     * @brief Cadastra um novo Cliente no sistema.
     * @param requestDTO DTO com os dados do Cliente a ser cadastrado.
     * @return DTO com os dados do Cliente cadastrado.
     * @throws BusinessException Se o CPF já estiver cadastrado ou o User associado
     *                           não for encontrado.
     */
    @Transactional
    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO requestDTO) {
        // 1. Verificar pré-requisito: User associado deve existir
        UUID userId = requestDTO.getUsuario().getId(); // Presume que o ID do User vem no DTO
        User user = repositorioUser.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "Usuário associado com ID " + userId + " não encontrado."));

        // 2. Validação de duplicidade de CPF
        if (repositorioCliente.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_CPF, "CPF já cadastrado para outro cliente.");
        }

        // 3. Mapear DTO para entidade
        Cliente cliente = clienteMapper.toEntity(requestDTO);
        cliente.setUsuario(user); // Associar o User encontrado

        // 4. Associar endereços à pessoa (Cliente)
        Set<Endereco> enderecos = requestDTO.getEnderecos().stream()
                .map(enderecoDTO -> {
                    Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
                    endereco.setPessoa(cliente); // Garante a associação bidirecional
                    return endereco;
                })
                .collect(Collectors.toSet());
        cliente.setEnderecos(enderecos);

        // 5. Salvar a entidade
        Cliente savedCliente = repositorioCliente.save(cliente);

        // 6. Mapear entidade salva para DTO de resposta
        return clienteMapper.toResponseDTO(savedCliente);
    }

    /**
     * @brief Busca um Cliente pelo ID.
     * @param id ID do Cliente a ser buscado.
     * @return DTO com os dados do Cliente encontrado.
     * @throws BusinessException Se o Cliente não for encontrado.
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorId(UUID id) {
        Cliente cliente = repositorioCliente.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Cliente com ID " + id + " não encontrado."));
        return clienteMapper.toResponseDTO(cliente);
    }

    /**
     * @brief Lista todos os Clientes com paginação.
     * @param pageable Objeto Pageable para configuração da paginação.
     * @return Página de DTOs de Clientes.
     */
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        return repositorioCliente.findAll(pageable)
                .map(clienteMapper::toResponseDTO);
    }

    /**
     * @brief Atualiza os dados de um Cliente existente.
     * @param id         ID do Cliente a ser atualizado.
     * @param requestDTO DTO com os dados atualizados do Cliente.
     * @return DTO com os dados do Cliente atualizado.
     * @throws BusinessException Se o Cliente não for encontrado ou se houver
     *                           duplicidade de CPF.
     */
    @Transactional
    public ClienteResponseDTO atualizarCliente(UUID id, ClienteRequestDTO requestDTO) {
        Cliente existingCliente = repositorioCliente.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Cliente com ID " + id + " não encontrado para atualização."));

        // 1. Validação de duplicidade de CPF (se o CPF foi alterado)
        if (!existingCliente.getCpf().equals(requestDTO.getCpf())) {
            if (repositorioCliente.findByCpf(requestDTO.getCpf()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_CPF, "Novo CPF já cadastrado para outro cliente.");
            }
        }

        // 2. Atualizar campos básicos da Pessoa e Cliente
        existingCliente.setNomeCompleto(requestDTO.getNomeCompleto());
        existingCliente.setCpf(requestDTO.getCpf());
        existingCliente.setDataNascimento(requestDTO.getDataNascimento());
        existingCliente.setStatus(requestDTO.getStatus()); // Campo específico de Cliente
        // existingCliente.setTipo(requestDTO.getTipo()); // Campo específico de Cliente

        // 3. Atualizar endereços (lógica de sincronização)
        existingCliente.getEnderecos().clear(); // Limpa os endereços existentes
        requestDTO.getEnderecos().forEach(enderecoDTO -> {
            Endereco newEndereco = enderecoMapper.toEntity(enderecoDTO);
            newEndereco.setPessoa(existingCliente);
            existingCliente.getEnderecos().add(newEndereco);
        });

        // 4. Atualizar telefones
        existingCliente.getTelefones().clear();
        if (requestDTO.getTelefones() != null) {
            existingCliente.getTelefones().addAll(requestDTO.getTelefones());
        }

        // 5. Salvar a entidade atualizada
        Cliente updatedCliente = repositorioCliente.save(existingCliente);

        return clienteMapper.toResponseDTO(updatedCliente);
    }

    /**
     * @brief Exclui um Cliente pelo ID.
     * @param id ID do Cliente a ser excluído.
     * @throws BusinessException Se o Cliente não for encontrado.
     */
    @Transactional
    public void excluirCliente(UUID id) {
        if (!repositorioCliente.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Cliente com ID " + id + " não encontrado para exclusão.");
        }
        // A exclusão em cascata do Endereco e User (se configurado no mapeamento) será
        // tratada pelo JPA
        repositorioCliente.deleteById(id);
    }
}
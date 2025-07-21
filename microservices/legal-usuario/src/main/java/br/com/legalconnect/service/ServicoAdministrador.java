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
import br.com.legalconnect.dto.AdministradorRequestDTO;
import br.com.legalconnect.dto.AdministradorResponseDTO;
import br.com.legalconnect.entity.Administrador;
import br.com.legalconnect.entity.Endereco;
import br.com.legalconnect.entity.User;
import br.com.legalconnect.mapper.AdministradorMapper;
import br.com.legalconnect.mapper.EnderecoMapper;
import br.com.legalconnect.repository.RepositorioAdministrador;
import br.com.legalconnect.repository.RepositorioUser;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoAdministrador
 * @brief Serviço de domínio para gerenciar operações relacionadas a
 *        Administradores.
 *        Contém a lógica de negócio para criação, busca, atualização e exclusão
 *        de Administradores.
 */
@Service
@RequiredArgsConstructor
public class ServicoAdministrador {

    private final RepositorioAdministrador repositorioAdministrador;
    private final RepositorioUser repositorioUser;
    private final AdministradorMapper administradorMapper;
    private final EnderecoMapper enderecoMapper;

    /**
     * @brief Cadastra um novo Administrador no sistema.
     * @param requestDTO DTO com os dados do Administrador a ser cadastrado.
     * @return DTO com os dados do Administrador cadastrado.
     * @throws BusinessException Se o CPF já estiver cadastrado ou o User associado
     *                           não for encontrado.
     */
    @Transactional
    public AdministradorResponseDTO cadastrarAdministrador(AdministradorRequestDTO requestDTO) {
        // 1. Verificar pré-requisito: User associado deve existir
        UUID userId = requestDTO.getUsuario().getId(); // Presume que o ID do User vem no DTO
        User user = repositorioUser.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "Usuário associado com ID " + userId + " não encontrado."));

        // 2. Validação de duplicidade de CPF
        if (repositorioAdministrador.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_CPF, "CPF já cadastrado para outro administrador.");
        }

        // 3. Mapear DTO para entidade
        Administrador administrador = administradorMapper.toEntity(requestDTO);
        administrador.setUsuario(user); // Associar o User encontrado

        // 4. Associar endereços à pessoa (Administrador)
        Set<Endereco> enderecos = requestDTO.getEnderecos().stream()
                .map(enderecoDTO -> {
                    Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
                    endereco.setPessoa(administrador); // Garante a associação bidirecional
                    return endereco;
                })
                .collect(Collectors.toSet());
        administrador.setEnderecos(enderecos);

        // 5. Salvar a entidade
        Administrador savedAdministrador = repositorioAdministrador.save(administrador);

        // 6. Mapear entidade salva para DTO de resposta
        return administradorMapper.toResponseDTO(savedAdministrador);
    }

    /**
     * @brief Busca um Administrador pelo ID.
     * @param id ID do Administrador a ser buscado.
     * @return DTO com os dados do Administrador encontrado.
     * @throws BusinessException Se o Administrador não for encontrado.
     */
    @Transactional(readOnly = true)
    public AdministradorResponseDTO buscarAdministradorPorId(UUID id) {
        Administrador administrador = repositorioAdministrador.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Administrador com ID " + id + " não encontrado."));
        return administradorMapper.toResponseDTO(administrador);
    }

    /**
     * @brief Lista todos os Administradores com paginação.
     * @param pageable Objeto Pageable para configuração da paginação.
     * @return Página de DTOs de Administradores.
     */
    @Transactional(readOnly = true)
    public Page<AdministradorResponseDTO> listarAdministradores(Pageable pageable) {
        return repositorioAdministrador.findAll(pageable)
                .map(administradorMapper::toResponseDTO);
    }

    /**
     * @brief Atualiza os dados de um Administrador existente.
     * @param id         ID do Administrador a ser atualizado.
     * @param requestDTO DTO com os dados atualizados do Administrador.
     * @return DTO com os dados do Administrador atualizado.
     * @throws BusinessException Se o Administrador não for encontrado ou se houver
     *                           duplicidade de CPF.
     */
    @Transactional
    public AdministradorResponseDTO atualizarAdministrador(UUID id, AdministradorRequestDTO requestDTO) {
        Administrador existingAdministrador = repositorioAdministrador.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Administrador com ID " + id + " não encontrado para atualização."));

        // 1. Validação de duplicidade de CPF (se o CPF foi alterado)
        if (!existingAdministrador.getCpf().equals(requestDTO.getCpf())) {
            if (repositorioAdministrador.findByCpf(requestDTO.getCpf()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_CPF, "Novo CPF já cadastrado para outro administrador.");
            }
        }

        // 2. Atualizar campos básicos da Pessoa e Administrador
        // O mapper precisa de um método de atualização, ou fazer manualmente
        existingAdministrador.setNomeCompleto(requestDTO.getNomeCompleto());
        existingAdministrador.setCpf(requestDTO.getCpf());
        existingAdministrador.setDataNascimento(requestDTO.getDataNascimento());
        existingAdministrador.setStatus(requestDTO.getStatus()); // Campo específico de Administrador

        // 3. Atualizar endereços (lógica de sincronização)
        existingAdministrador.getEnderecos().clear(); // Limpa os endereços existentes
        requestDTO.getEnderecos().forEach(enderecoDTO -> {
            // Reutiliza o mapper para converter DTO para Endereco
            Endereco newEndereco = enderecoMapper.toEntity(enderecoDTO);
            newEndereco.setPessoa(existingAdministrador); // Garante a associação bidirecional
            existingAdministrador.getEnderecos().add(newEndereco);
        });

        // 4. Atualizar telefones
        existingAdministrador.getTelefones().clear();
        if (requestDTO.getTelefones() != null) {
            existingAdministrador.getTelefones().addAll(requestDTO.getTelefones());
        }

        // 5. Salvar a entidade atualizada
        Administrador updatedAdministrador = repositorioAdministrador.save(existingAdministrador);

        return administradorMapper.toResponseDTO(updatedAdministrador);
    }

    /**
     * @brief Exclui um Administrador pelo ID.
     * @param id ID do Administrador a ser excluído.
     * @throws BusinessException Se o Administrador não for encontrado.
     */
    @Transactional
    public void excluirAdministrador(UUID id) {
        if (!repositorioAdministrador.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Administrador com ID " + id + " não encontrado para exclusão.");
        }
        // A exclusão em cascata do Endereco e User (se configurado no mapeamento) será
        // tratada pelo JPA
        repositorioAdministrador.deleteById(id);
    }
}
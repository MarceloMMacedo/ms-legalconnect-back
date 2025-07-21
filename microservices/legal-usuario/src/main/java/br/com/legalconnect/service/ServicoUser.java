package br.com.legalconnect.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.common.exception.BusinessException; // Importa da common-lib
import br.com.legalconnect.common.exception.ErrorCode; // Importa da common-lib
import br.com.legalconnect.dto.UserResponseDTO;
import br.com.legalconnect.entity.User;
import br.com.legalconnect.mapper.UserMapper;
import br.com.legalconnect.repository.RepositorioUser;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoUser
 * @brief Serviço de domínio para gerenciar operações relacionadas a Users.
 *        **Nota:** Este serviço é primariamente para busca e validação de Users
 *        existentes
 *        para associação com entidades Pessoa (Profissional, Cliente,
 *        Administrador).
 *        A criação e gestão principal de Users é responsabilidade de outro
 *        microsserviço (ex: auth-service).
 */
@Service
@RequiredArgsConstructor
public class ServicoUser {

    private final RepositorioUser repositorioUser;
    private final UserMapper userMapper;

    /**
     * @brief Busca um User pelo ID.
     * @param id ID do User a ser buscado.
     * @return DTO com os dados do User encontrado.
     * @throws BusinessException Se o User não for encontrado.
     */
    @Transactional(readOnly = true)
    public UserResponseDTO buscarUserPorId(UUID id) {
        User user = repositorioUser.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "Usuário com ID " + id + " não encontrado."));
        return userMapper.toResponseDTO(user);
    }

    /**
     * @brief Lista todos os Users.
     * @return Lista de DTOs de Users.
     */
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarTodosUsers() {
        return repositorioUser.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * @brief Verifica se um User existe pelo ID.
     * @param id ID do User a ser verificado.
     * @return true se o User existe, false caso contrário.
     */
    @Transactional(readOnly = true)
    public boolean userExiste(UUID id) {
        return repositorioUser.existsById(id);
    }

    /**
     * @brief Busca um User pelo CPF.
     * @param cpf O CPF do usuário.
     * @return Um Optional contendo o DTO do User, se encontrado.
     */
    @Transactional(readOnly = true)
    public Optional<UserResponseDTO> buscarUserPorCpf(String cpf) {
        return repositorioUser.findByCpf(cpf)
                .map(userMapper::toResponseDTO);
    }
}
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
import br.com.legalconnect.dto.ProfissionalRequestDTO;
import br.com.legalconnect.dto.ProfissionalResponseDTO;
import br.com.legalconnect.entity.Empresa;
import br.com.legalconnect.entity.Endereco;
import br.com.legalconnect.entity.Plano;
import br.com.legalconnect.entity.Profissional;
import br.com.legalconnect.entity.User;
import br.com.legalconnect.mapper.EnderecoMapper;
import br.com.legalconnect.mapper.ProfissionalMapper;
import br.com.legalconnect.repository.RepositorioEmpresa;
import br.com.legalconnect.repository.RepositorioPlano;
import br.com.legalconnect.repository.RepositorioProfissional;
import br.com.legalconnect.repository.RepositorioUser;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoProfissional
 * @brief Serviço de domínio para gerenciar operações relacionadas a
 *        Profissionais.
 *        Contém a lógica de negócio para criação, busca, atualização e exclusão
 *        de Profissionais.
 */
@Service
@RequiredArgsConstructor
public class ServicoProfissional {

    private final RepositorioProfissional repositorioProfissional;
    private final RepositorioUser repositorioUser;
    private final RepositorioPlano repositorioPlano;
    private final RepositorioEmpresa repositorioEmpresa;
    private final ProfissionalMapper profissionalMapper;

    private final EnderecoMapper enderecoMapper;

    /**
     * @brief Cadastra um novo Profissional no sistema.
     * @param requestDTO DTO com os dados do Profissional a ser cadastrado.
     * @return DTO com os dados do Profissional cadastrado.
     * @throws BusinessException Se o CPF ou OAB já estiverem cadastrados, ou se o
     *                           User/Plano/Empresa associado não for encontrado.
     */
    @Transactional
    public ProfissionalResponseDTO cadastrarProfissional(ProfissionalRequestDTO requestDTO) {
        // 1. Verificar pré-requisito: User associado deve existir
        UUID userId = requestDTO.getUsuario().getId(); // Presume que o ID do User vem no DTO
        User user = repositorioUser.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "Usuário associado com ID " + userId + " não encontrado."));

        // 2. Validação de duplicidade de CPF e OAB
        if (repositorioProfissional.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_CPF, "CPF já cadastrado para outro profissional.");
        }
        if (repositorioProfissional.findByNumeroOab(requestDTO.getNumeroOab()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT,
                    "Número da OAB já cadastrado para outro profissional.");
        }

        // 3. Buscar Plano e Empresa (se aplicável)
        Plano plano = repositorioPlano.findById(requestDTO.getPlanoId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Plano com ID " + requestDTO.getPlanoId() + " não encontrado."));

        Empresa empresa = null;
        if (requestDTO.getEmpresaId() != null) {
            empresa = repositorioEmpresa.findById(requestDTO.getEmpresaId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "Empresa com ID " + requestDTO.getEmpresaId() + " não encontrada."));
        }

        // 4. Mapear DTO para entidade
        Profissional profissional = profissionalMapper.toEntity(requestDTO);
        profissional.setUsuario(user); // Associar o User encontrado
        profissional.setPlano(plano); // Associar o Plano encontrado
        profissional.setEmpresa(empresa); // Associar a Empresa (pode ser null)

        // 5. Associar endereços à pessoa (Profissional)
        Set<Endereco> enderecos = requestDTO.getEnderecos().stream()
                .map(enderecoDTO -> {
                    // Reutiliza o mapper para converter DTO para Endereco
                    Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
                    endereco.setPessoa(profissional); // Garante a associação bidirecional
                    return endereco;
                })
                .collect(Collectors.toSet());
        profissional.setEnderecos(enderecos);

        // 6. Salvar a entidade
        Profissional savedProfissional = repositorioProfissional.save(profissional);

        // 7. Mapear entidade salva para DTO de resposta
        return profissionalMapper.toResponseDTO(savedProfissional);
    }

    /**
     * @brief Busca um Profissional pelo ID.
     * @param id ID do Profissional a ser buscado.
     * @return DTO com os dados do Profissional encontrado.
     * @throws BusinessException Se o Profissional não for encontrado.
     */
    @Transactional(readOnly = true)
    public ProfissionalResponseDTO buscarProfissionalPorId(UUID id) {
        Profissional profissional = repositorioProfissional.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Profissional com ID " + id + " não encontrado."));
        return profissionalMapper.toResponseDTO(profissional);
    }

    /**
     * @brief Lista todos os Profissionais com paginação.
     * @param pageable Objeto Pageable para configuração da paginação.
     * @return Página de DTOs de Profissionais.
     */
    @Transactional(readOnly = true)
    public Page<ProfissionalResponseDTO> listarProfissionais(Pageable pageable) {
        return repositorioProfissional.findAll(pageable)
                .map(profissionalMapper::toResponseDTO);
    }

    /**
     * @brief Atualiza os dados de um Profissional existente.
     * @param id         ID do Profissional a ser atualizado.
     * @param requestDTO DTO com os dados atualizados do Profissional.
     * @return DTO com os dados do Profissional atualizado.
     * @throws BusinessException Se o Profissional não for encontrado, ou se houver
     *                           duplicidade de CPF/OAB, ou se Plano/Empresa não for
     *                           encontrado.
     */
    @Transactional
    public ProfissionalResponseDTO atualizarProfissional(UUID id, ProfissionalRequestDTO requestDTO) {
        Profissional existingProfissional = repositorioProfissional.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Profissional com ID " + id + " não encontrado para atualização."));

        // 1. Validação de duplicidade de CPF (se o CPF foi alterado)
        if (!existingProfissional.getCpf().equals(requestDTO.getCpf())) {
            if (repositorioProfissional.findByCpf(requestDTO.getCpf()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_CPF, "Novo CPF já cadastrado para outro profissional.");
            }
        }
        // 2. Validação de duplicidade de OAB (se a OAB foi alterada)
        if (!existingProfissional.getNumeroOab().equals(requestDTO.getNumeroOab())) {
            if (repositorioProfissional.findByNumeroOab(requestDTO.getNumeroOab()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT,
                        "Novo número da OAB já cadastrado para outro profissional.");
            }
        }

        // 3. Buscar Plano e Empresa (se aplicável)
        Plano plano = repositorioPlano.findById(requestDTO.getPlanoId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Plano com ID " + requestDTO.getPlanoId() + " não encontrado."));

        Empresa empresa = null;
        if (requestDTO.getEmpresaId() != null) {
            empresa = repositorioEmpresa.findById(requestDTO.getEmpresaId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "Empresa com ID " + requestDTO.getEmpresaId() + " não encontrada."));
        }

        // 4. Atualizar campos básicos da Pessoa e Profissional
        existingProfissional.setNomeCompleto(requestDTO.getNomeCompleto());
        existingProfissional.setCpf(requestDTO.getCpf());
        existingProfissional.setDataNascimento(requestDTO.getDataNascimento());
        existingProfissional.setNumeroOab(requestDTO.getNumeroOab());
        existingProfissional.setStatusProfissional(requestDTO.getStatusProfissional());
        existingProfissional.setUsaMarketplace(requestDTO.getUsaMarketplace());
        existingProfissional.setFazParteDePlano(requestDTO.getFazParteDePlano());
        existingProfissional.setPlano(plano);
        existingProfissional.setEmpresa(empresa);

        // 5. Atualizar endereços (lógica de sincronização)
        existingProfissional.getEnderecos().clear(); // Limpa os endereços existentes
        requestDTO.getEnderecos().forEach(enderecoDTO -> {
            Endereco newEndereco = enderecoMapper.toEntity(enderecoDTO);
            newEndereco.setPessoa(existingProfissional);
            existingProfissional.getEnderecos().add(newEndereco);
        });

        // 6. Atualizar telefones
        existingProfissional.getTelefones().clear();
        if (requestDTO.getTelefones() != null) {
            existingProfissional.getTelefones().addAll(requestDTO.getTelefones());
        }

        // 7. Salvar a entidade atualizada
        Profissional updatedProfissional = repositorioProfissional.save(existingProfissional);

        return profissionalMapper.toResponseDTO(updatedProfissional);
    }

    /**
     * @brief Exclui um Profissional pelo ID.
     * @param id ID do Profissional a ser excluído.
     * @throws BusinessException Se o Profissional não for encontrado.
     */
    @Transactional
    public void excluirProfissional(UUID id) {
        if (!repositorioProfissional.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Profissional com ID " + id + " não encontrado para exclusão.");
        }
        // A exclusão em cascata do Endereco e User (se configurado no mapeamento) será
        // tratada pelo JPA
        repositorioProfissional.deleteById(id);
    }
}
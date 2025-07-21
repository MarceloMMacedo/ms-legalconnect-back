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
import br.com.legalconnect.dto.EmpresaRequestDTO;
import br.com.legalconnect.dto.EmpresaResponseDTO;
import br.com.legalconnect.entity.Empresa;
import br.com.legalconnect.entity.Endereco;
import br.com.legalconnect.mapper.EmpresaMapper;
import br.com.legalconnect.mapper.EnderecoMapper;
import br.com.legalconnect.repository.RepositorioEmpresa;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoEmpresa
 * @brief Serviço de domínio para gerenciar operações relacionadas a Empresas.
 *        Contém a lógica de negócio para criação, busca, atualização e exclusão
 *        de Empresas.
 */
@Service
@RequiredArgsConstructor
public class ServicoEmpresa {

    private final RepositorioEmpresa repositorioEmpresa;
    private final EmpresaMapper empresaMapper;

    private final EnderecoMapper enderecoMapper;

    /**
     * @brief Cadastra uma nova Empresa no sistema.
     * @param requestDTO DTO com os dados da Empresa a ser cadastrada.
     * @return DTO com os dados da Empresa cadastrada.
     * @throws BusinessException Se o CNPJ já estiver cadastrado.
     */
    @Transactional
    public EmpresaResponseDTO cadastrarEmpresa(EmpresaRequestDTO requestDTO) {
        // 1. Validação de duplicidade de CNPJ
        if (repositorioEmpresa.findByCnpj(requestDTO.getCnpj()).isPresent()) {
            throw new BusinessException(ErrorCode.INVALID_CNPJ, "CNPJ já cadastrado para outra empresa.");
        }

        // 2. Mapear DTO para entidade
        Empresa empresa = empresaMapper.toEntity(requestDTO);

        // 3. Associar endereços à empresa
        Set<Endereco> enderecos = requestDTO.getEnderecos().stream()
                .map(enderecoDTO -> {
                    // Reutiliza o mapper para converter DTO para Endereco
                    Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
                    endereco.setEmpresa(empresa); // Garante a associação bidirecional
                    return endereco;
                })
                .collect(Collectors.toSet());
        empresa.setEnderecos(enderecos);

        // 4. Salvar a entidade
        Empresa savedEmpresa = repositorioEmpresa.save(empresa);

        // 5. Mapear entidade salva para DTO de resposta
        return empresaMapper.toResponseDTO(savedEmpresa);
    }

    /**
     * @brief Busca uma Empresa pelo ID.
     * @param id ID da Empresa a ser buscada.
     * @return DTO com os dados da Empresa encontrada.
     * @throws BusinessException Se a Empresa não for encontrada.
     */
    @Transactional(readOnly = true)
    public EmpresaResponseDTO buscarEmpresaPorId(UUID id) {
        Empresa empresa = repositorioEmpresa.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Empresa com ID " + id + " não encontrada."));
        return empresaMapper.toResponseDTO(empresa);
    }

    /**
     * @brief Lista todas as Empresas com paginação.
     * @param pageable Objeto Pageable para configuração da paginação.
     * @return Página de DTOs de Empresas.
     */
    @Transactional(readOnly = true)
    public Page<EmpresaResponseDTO> listarEmpresas(Pageable pageable) {
        return repositorioEmpresa.findAll(pageable)
                .map(empresaMapper::toResponseDTO);
    }

    /**
     * @brief Atualiza os dados de uma Empresa existente.
     * @param id         ID da Empresa a ser atualizada.
     * @param requestDTO DTO com os dados atualizados da Empresa.
     * @return DTO com os dados da Empresa atualizada.
     * @throws BusinessException Se a Empresa não for encontrada ou se houver
     *                           duplicidade de CNPJ.
     */
    @Transactional
    public EmpresaResponseDTO atualizarEmpresa(UUID id, EmpresaRequestDTO requestDTO) {
        Empresa existingEmpresa = repositorioEmpresa.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Empresa com ID " + id + " não encontrada para atualização."));

        // 1. Validação de duplicidade de CNPJ (se o CNPJ foi alterado)
        if (!existingEmpresa.getCnpj().equals(requestDTO.getCnpj())) {
            if (repositorioEmpresa.findByCnpj(requestDTO.getCnpj()).isPresent()) {
                throw new BusinessException(ErrorCode.INVALID_CNPJ, "Novo CNPJ já cadastrado para outra empresa.");
            }
        }

        // 2. Atualizar campos básicos da Empresa
        existingEmpresa.setNomeFantasia(requestDTO.getNomeFantasia());
        existingEmpresa.setRazaoSocial(requestDTO.getRazaoSocial());
        existingEmpresa.setCnpj(requestDTO.getCnpj());
        existingEmpresa.setEmailContato(requestDTO.getEmailContato());

        // 3. Atualizar endereços (lógica de sincronização)
        existingEmpresa.getEnderecos().clear(); // Limpa os endereços existentes
        requestDTO.getEnderecos().forEach(enderecoDTO -> {
            Endereco newEndereco = enderecoMapper.toEntity(enderecoDTO);
            newEndereco.setEmpresa(existingEmpresa);
            existingEmpresa.getEnderecos().add(newEndereco);
        });

        // 4. Atualizar telefones
        existingEmpresa.getTelefones().clear();
        if (requestDTO.getTelefones() != null) {
            existingEmpresa.getTelefones().addAll(requestDTO.getTelefones());
        }

        // 5. Salvar a entidade atualizada
        Empresa updatedEmpresa = repositorioEmpresa.save(existingEmpresa);

        return empresaMapper.toResponseDTO(updatedEmpresa);
    }

    /**
     * @brief Exclui uma Empresa pelo ID.
     * @param id ID da Empresa a ser excluída.
     * @throws BusinessException Se a Empresa não for encontrada ou se possuir
     *                           profissionais associados.
     */
    @Transactional
    public void excluirEmpresa(UUID id) {
        Empresa empresa = repositorioEmpresa.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Empresa com ID " + id + " não encontrada para exclusão."));

        // Verificar se existem profissionais associados antes de excluir
        if (!empresa.getProfissionais().isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Empresa não pode ser excluída pois possui profissionais associados.");
        }

        // A exclusão em cascata do Endereco será tratada pelo JPA
        repositorioEmpresa.delete(empresa);
    }
}
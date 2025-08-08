package br.com.legalconnect.depoimento.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.depoimento.domain.enums.DepoimentoStatus;
import br.com.legalconnect.depoimento.domain.enums.TipoDepoimento;
import br.com.legalconnect.depoimento.domain.model.Depoimento;
import br.com.legalconnect.depoimento.domain.service.DepoimentoService;
import br.com.legalconnect.depoimento.dto.DepoimentoRequestDTO;
import br.com.legalconnect.depoimento.dto.DepoimentoResponseDTO;
import br.com.legalconnect.depoimento.repository.DepoimentoJpaRepository;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação para o módulo de Depoimentos.
 * Orquestra operações entre DTOs, e o serviço de domínio, realizando mapeamento
 * manual com Builder.
 * Contém regras de negócio de alto nível e validações.
 */
@Service
@RequiredArgsConstructor
public class DepoimentoAppService {

    private final DepoimentoService domainService;
    private final DepoimentoJpaRepository repository;
    private final UserServiceImpl userService;

    /**
     * Converte um DepoimentoRequestDTO para uma entidade Depoimento.
     * 
     * @param dto O DTO de requisição.
     * @return A entidade Depoimento.
     */
    private Depoimento toEntity(DepoimentoRequestDTO dto) {
        return Depoimento.builder()
                .texto(dto.getTexto())
                .nome(dto.getNome())
                .local(dto.getLocal())
                .fotoUrl(dto.getFotoUrl())
                .userId(dto.getUserId())
                .tipoDepoimento(TipoDepoimento.valueOf(dto.getTipoDepoimento().toUpperCase()))
                // O status é definido posteriormente na lógica de negócio, não aqui
                .build();
    }

    /**
     * Converte uma entidade Depoimento para um DepoimentoResponseDTO.
     * 
     * @param entity A entidade Depoimento.
     * @return O DTO de resposta.
     */
    private DepoimentoResponseDTO toResponse(Depoimento entity) {
        return DepoimentoResponseDTO.builder()
                .id(entity.getId())
                .texto(entity.getTexto())
                .nome(entity.getNome())
                .local(entity.getLocal())
                .fotoUrl(entity.getFotoUrl())
                .userId(entity.getUserId())
                .tipoDepoimento(entity.getTipoDepoimento().name())
                .status(entity.getStatus().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Atualiza uma entidade Depoimento existente a partir de um DTO de requisição.
     * 
     * @param dto    O DTO de requisição.
     * @param entity A entidade a ser atualizada.
     */
    private void updateEntityFromDto(DepoimentoRequestDTO dto, Depoimento entity) {
        entity.setTexto(dto.getTexto());
        entity.setNome(dto.getNome());
        entity.setLocal(dto.getLocal());
        entity.setFotoUrl(dto.getFotoUrl());
        entity.setUserId(dto.getUserId());
        entity.setTipoDepoimento(TipoDepoimento.valueOf(dto.getTipoDepoimento().toUpperCase()));
        // O status é atualizado separadamente pela lógica de negócio no AppService
    }

    /**
     * Converte uma String para DepoimentoStatus.
     * 
     * @param status String do status.
     * @return DepoimentoStatus ou null se a string for nula ou vazia.
     */
    private DepoimentoStatus mapStringToDepoimentoStatus(String status) {
        return (status != null && !status.isEmpty()) ? DepoimentoStatus.valueOf(status.toUpperCase()) : null;
    }

    /**
     * Cria um novo depoimento a partir de um DTO de requisição.
     * Implementa regras de negócio para status e validação de usuário.
     * 
     * @param request O DTO contendo os dados do novo depoimento.
     * @return O DTO de resposta do depoimento criado.
     * @throws BusinessException se o texto do depoimento for muito longo,
     *                           o usuário não for encontrado ou o status inicial
     *                           for inválido.
     */
    public DepoimentoResponseDTO criarDepoimento(DepoimentoRequestDTO request) {
        if (request.getTexto().length() > 500) {
            throw new BusinessException(ErrorCode.DADOS_INVALIDOS, ErrorCode.DADOS_INVALIDOS.getMessage());
        }

        if (!userService.userExists(request.getUserId())) {
            throw new BusinessException(ErrorCode.USER_NAO_ENCONTRADO, ErrorCode.USER_NAO_ENCONTRADO.getMessage());
        }

        Depoimento depoimento = toEntity(request); // Usando o método toEntity manual

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PLATAFORMA_ADMIN"));

        if (!isAdmin && request.getStatus() != null
                && !request.getStatus().equalsIgnoreCase(DepoimentoStatus.PENDENTE.name())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, ErrorCode.FORBIDDEN_ACCESS.getMessage());
        }

        if (request.getStatus() != null && isAdmin) {
            depoimento.setStatus(DepoimentoStatus.valueOf(request.getStatus().toUpperCase()));
        } else {
            depoimento.setStatus(DepoimentoStatus.PENDENTE);
        }

        Depoimento salvo = domainService.salvar(depoimento);
        return toResponse(salvo); // Usando o método toResponse manual
    }

    /**
     * Atualiza um depoimento existente.
     * 
     * @param id      O ID do depoimento a ser atualizado.
     * @param request O DTO contendo os dados atualizados.
     * @return O DTO de resposta do depoimento atualizado.
     * @throws BusinessException se o depoimento não for encontrado, o texto for
     *                           muito longo,
     *                           o usuário não for encontrado ou o status for
     *                           inválido para a operação.
     */
    public DepoimentoResponseDTO atualizarDepoimento(UUID id, DepoimentoRequestDTO request) {
        if (request.getTexto().length() > 500) {
            throw new BusinessException(ErrorCode.DADOS_INVALIDOS, ErrorCode.DADOS_INVALIDOS.getMessage());
        }

        if (!userService.userExists(request.getUserId())) {
            throw new BusinessException(ErrorCode.USER_NAO_ENCONTRADO, ErrorCode.USER_NAO_ENCONTRADO.getMessage());
        }

        return domainService.buscarPorId(id)
                .map(depoimentoExistente -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_PLATAFORMA_ADMIN"));

                    DepoimentoStatus novoStatus = mapStringToDepoimentoStatus(request.getStatus());

                    if (novoStatus != null && novoStatus != depoimentoExistente.getStatus() && !isAdmin) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS,
                                ErrorCode.FORBIDDEN_ACCESS.getMessage());
                    }

                    updateEntityFromDto(request, depoimentoExistente); // Usando o método updateEntityFromDto manual
                    if (novoStatus != null && isAdmin) {
                        depoimentoExistente.setStatus(novoStatus);
                    }

                    return toResponse(domainService.salvar(depoimentoExistente)); // Usando o método toResponse manual
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                        ErrorCode.ENTIDADE_NAO_ENCONTRADA.getMessage()));
    }

    /**
     * Exclui um depoimento pelo seu ID.
     * 
     * @param id O ID do depoimento a ser excluído.
     * @throws BusinessException se o depoimento não for encontrado.
     */
    public void excluirDepoimento(UUID id) {
        if (!domainService.buscarPorId(id).isPresent()) {
            throw new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                    ErrorCode.ENTIDADE_NAO_ENCONTRADA.getMessage());
        }
        domainService.excluir(id);
    }

    /**
     * Aprova um depoimento. Apenas administradores podem realizar esta operação.
     * 
     * @param id O ID do depoimento a ser aprovado.
     * @return O DTO do depoimento aprovado.
     * @throws BusinessException se o depoimento não for encontrado ou se o status
     *                           já for APROVADO.
     */
    public DepoimentoResponseDTO aprovarDepoimento(UUID id) {
        return domainService.buscarPorId(id)
                .map(depoimento -> {
                    if (depoimento.getStatus() == DepoimentoStatus.APROVADO) {
                        // throw new BusinessException(ErrorCode.DEPOIMENTO_ALREADY_APPROVED,
                        // ErrorCode.DEPOIMENTO_ALREADY_APPROVED.getMessage());
                    }
                    return toResponse(domainService.alterarStatus(id, DepoimentoStatus.APROVADO).get()); // Usando o
                                                                                                         // método
                                                                                                         // toResponse
                                                                                                         // manual
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                        ErrorCode.ENTIDADE_NAO_ENCONTRADA.getMessage()));
    }

    /**
     * Reprova um depoimento. Apenas administradores podem realizar esta operação.
     * 
     * @param id O ID do depoimento a ser reprovado.
     * @return O DTO do depoimento reprovado.
     * @throws BusinessException se o depoimento não for encontrado ou se o status
     *                           já for REPROVADO.
     */
    public DepoimentoResponseDTO reprovarDepoimento(UUID id) {
        return domainService.buscarPorId(id)
                .map(depoimento -> {
                    if (depoimento.getStatus() == DepoimentoStatus.REPROVADO) {
                        // throw new BusinessException(ErrorCode.DEPOIMENTO_ALREADY_REJECTED,
                        // ErrorCode.DEPOIMENTO_ALREADY_REJECTED.getMessage());
                    }
                    return toResponse(domainService.alterarStatus(id, DepoimentoStatus.REPROVADO).get()); // Usando o
                                                                                                          // método
                                                                                                          // toResponse
                                                                                                          // manual
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                        ErrorCode.ENTIDADE_NAO_ENCONTRADA.getMessage()));
    }

    /**
     * Lista todos os depoimentos, convertendo-os para DTOs de resposta.
     * 
     * @return Uma lista de DTOs de resposta de depoimentos.
     */
    public List<DepoimentoResponseDTO> listarTodos() {
        return domainService.listarTodos().stream()
                .map(this::toResponse) // Usando o método toResponse manual
                .collect(Collectors.toList());
    }

    /**
     * Busca um depoimento pelo seu ID e o converte para DTO de resposta.
     * 
     * @param id O ID do depoimento.
     * @return O DTO de resposta do depoimento.
     * @throws BusinessException se o depoimento não for encontrado.
     */
    public DepoimentoResponseDTO buscarPorId(UUID id) {
        return domainService.buscarPorId(id)
                .map(this::toResponse) // Usando o método toResponse manual
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                        ErrorCode.ENTIDADE_NAO_ENCONTRADA.getMessage()));
    }

    /**
     * Lista depoimentos para a página inicial, com opções de limite e
     * aleatoriedade.
     * Esta é uma responsabilidade do AppService, pois é um caso de uso específico.
     * 
     * @param limit  O número máximo de depoimentos a serem retornados.
     * @param random Booleano indicando se a busca deve ser aleatória.
     * @return Uma lista de DTOs de resposta de depoimentos.
     */
    public List<DepoimentoResponseDTO> listarParaHome(int limit, boolean random) {
        List<Depoimento> depoimentos;
        if (random) {
            depoimentos = repository.buscarAleatoriosAprovados(limit);
        } else {
            depoimentos = repository.findTop5ByStatusOrderByCreatedAtDesc(DepoimentoStatus.APROVADO);
        }
        return depoimentos.stream()
                .map(this::toResponse) // Usando o método toResponse manual
                .collect(Collectors.toList());
    }
}
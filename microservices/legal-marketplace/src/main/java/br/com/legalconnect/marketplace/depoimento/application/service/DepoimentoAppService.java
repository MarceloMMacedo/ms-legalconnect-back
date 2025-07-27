package br.com.legalconnect.marketplace.depoimento.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication; // Importa Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Importa SecurityContextHolder
import org.springframework.stereotype.Service;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import br.com.legalconnect.marketplace.depoimento.application.dto.DepoimentoRequestDTO;
import br.com.legalconnect.marketplace.depoimento.application.dto.DepoimentoResponseDTO;
import br.com.legalconnect.marketplace.depoimento.application.mapper.DepoimentoMapper;
import br.com.legalconnect.marketplace.depoimento.domain.enums.DepoimentoStatus; // Importado o novo enum
import br.com.legalconnect.marketplace.depoimento.domain.model.Depoimento;
import br.com.legalconnect.marketplace.depoimento.domain.service.DepoimentoService;
import br.com.legalconnect.marketplace.depoimento.infrastructure.persistence.DepoimentoJpaRepository; // Novo import para consultas diretas
import br.com.legalconnect.marketplace.depoimento.user.domain.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de aplicação para o módulo de Depoimentos.
 * Orquestra operações entre DTOs, mappers e o serviço de domínio.
 * Contém regras de negócio de alto nível e validações.
 */
@Service
@RequiredArgsConstructor
public class DepoimentoAppService {

    private final DepoimentoService domainService;
    private final DepoimentoJpaRepository repository; // Injetado para consultas de caso de uso
    private final DepoimentoMapper mapper;
    private final UserService userService; // Injeção do UserService

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
            throw new BusinessException(ErrorCode.INVALID_REQUEST_PARAMETER,
                    "Texto do depoimento excede o limite de 500 caracteres.");
        }

        // 1. Validação da existência do usuário
        if (!userService.userExists(request.getUserId())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND,
                    "Usuário com ID " + request.getUserId() + " não encontrado.");
        }

        Depoimento depoimento = mapper.toEntity(request); // Mapeia os campos básicos

        // 2. Regra de negócio para o status inicial:
        // Se o usuário atual não for um ADMIN, o status só pode ser PENDENTE.
        // Se for ADMIN, pode definir qualquer status.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PLATAFORMA_ADMIN"));

        if (!isAdmin && request.getStatus() != null
                && !request.getStatus().equalsIgnoreCase(DepoimentoStatus.PENDENTE.name())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS,
                    "Somente administradores podem definir o status inicial como APROVADO ou REPROVADO.");
        }

        if (request.getStatus() != null && isAdmin) {
            depoimento.setStatus(DepoimentoStatus.valueOf(request.getStatus().toUpperCase()));
        } else {
            depoimento.setStatus(DepoimentoStatus.PENDENTE); // Default para PENDENTE se não for admin ou status não
                                                             // especificado
        }

        Depoimento salvo = domainService.salvar(depoimento);
        return mapper.toResponse(salvo);
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
            throw new BusinessException(ErrorCode.INVALID_REQUEST_PARAMETER,
                    "Texto do depoimento excede o limite de 500 caracteres.");
        }

        // 1. Validação da existência do usuário (se o userId for alterado ou para
        // garantir consistência)
        if (!userService.userExists(request.getUserId())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND,
                    "Usuário com ID " + request.getUserId() + " não encontrado.");
        }

        return domainService.buscarPorId(id)
                .map(depoimentoExistente -> {
                    // Validação de status: Apenas admins podem alterar o status
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_PLATAFORMA_ADMIN"));

                    DepoimentoStatus novoStatus = mapper.mapStringToDepoimentoStatus(request.getStatus());

                    if (novoStatus != null && novoStatus != depoimentoExistente.getStatus() && !isAdmin) {
                        throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS,
                                "Somente administradores podem alterar o status de um depoimento.");
                    }

                    mapper.updateEntityFromDto(request, depoimentoExistente); // Atualiza a entidade existente com os
                                                                              // novos dados
                    // Se o status veio no DTO e é admin, o mapper já aplicou. Senão, mantém o
                    // existente.
                    if (novoStatus != null && isAdmin) {
                        depoimentoExistente.setStatus(novoStatus); // Garante que o status do DTO seja aplicado se for
                                                                   // admin
                    }

                    return mapper.toResponse(domainService.salvar(depoimentoExistente)); // Salva e retorna o DTO
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Depoimento não encontrado com ID: " + id));
    }

    /**
     * Exclui um depoimento pelo seu ID.
     * 
     * @param id O ID do depoimento a ser excluído.
     * @throws BusinessException se o depoimento não for encontrado.
     */
    public void excluirDepoimento(UUID id) {
        if (!domainService.buscarPorId(id).isPresent()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Depoimento não encontrado com ID: " + id);
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
                        throw new BusinessException(ErrorCode.DEPOIMENTO_ALREADY_APPROVED,
                                "Depoimento já está aprovado.");
                    }
                    return mapper.toResponse(domainService.alterarStatus(id, DepoimentoStatus.APROVADO).get());
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Depoimento não encontrado com ID: " + id));
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
                        throw new BusinessException(ErrorCode.DEPOIMENTO_ALREADY_REJECTED,
                                "Depoimento já está reprovado.");
                    }
                    return mapper.toResponse(domainService.alterarStatus(id, DepoimentoStatus.REPROVADO).get());
                })
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Depoimento não encontrado com ID: " + id));
    }

    /**
     * Lista todos os depoimentos, convertendo-os para DTOs de resposta.
     * 
     * @return Uma lista de DTOs de resposta de depoimentos.
     */
    public List<DepoimentoResponseDTO> listarTodos() {
        return domainService.listarTodos().stream()
                .map(mapper::toResponse)
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
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Depoimento não encontrado com ID: " + id));
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
        // A lógica de negócio para "listar para home" reside aqui, no AppService,
        // pois é um caso de uso que pode envolver regras específicas (ex: apenas
        // aprovados).
        List<Depoimento> depoimentos;
        if (random) {
            depoimentos = repository.buscarAleatoriosAprovados(limit);
        } else {
            depoimentos = repository.findTop5ByStatusOrderByCreatedAtDesc(DepoimentoStatus.APROVADO);
        }
        return depoimentos.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
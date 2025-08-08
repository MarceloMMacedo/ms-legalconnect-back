package br.com.legalconnect.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.legalconnect.common.exception.BusinessException; // Importa da common-lib
import br.com.legalconnect.common.exception.ErrorCode; // Importa da common-lib
import br.com.legalconnect.dto.PlanoResponseDTO;
import br.com.legalconnect.entity.Plano;
import br.com.legalconnect.mapper.ProfissionalMapper; // Reutilizando mapper de profissional para plano
import br.com.legalconnect.repository.RepositorioPlano;
import lombok.RequiredArgsConstructor;

/**
 * @class ServicoPlano
 * @brief Serviço de domínio para gerenciar operações relacionadas a Planos.
 *        **Nota:** Este serviço é um placeholder. Em uma arquitetura de
 *        microsserviços real,
 *        a gestão de planos seria feita em um microsserviço dedicado (ex:
 *        marketplace ou assinatura).
 *        Aqui, ele apenas fornece métodos de busca para que outros serviços
 *        possam referenciar planos.
 */
@Service
@RequiredArgsConstructor
public class ServicoPlano {

    private final RepositorioPlano repositorioPlano;
    private final ProfissionalMapper profissionalMapper;

    /**
     * @brief Busca um Plano pelo ID.
     * @param id ID do Plano a ser buscado.
     * @return DTO com os dados do Plano encontrado.
     * @throws BusinessException Se o Plano não for encontrado.
     */
    @Transactional(readOnly = true)
    public PlanoResponseDTO buscarPlanoPorId(UUID id) {
        Plano plano = repositorioPlano.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Plano com ID " + id + " não encontrado."));
        return profissionalMapper.toPlanoResponseDTO(plano);
    }

    /**
     * @brief Lista todos os Planos disponíveis.
     * @return Lista de DTOs de Planos.
     */
    @Transactional(readOnly = true)
    public List<PlanoResponseDTO> listarTodosPlanos() {
        return repositorioPlano.findAll().stream()
                .map(profissionalMapper::toPlanoResponseDTO)
                .collect(Collectors.toList());
    }

    // Métodos para cadastrar, atualizar e excluir planos não são implementados
    // aqui,
    // pois seriam responsabilidade do microsserviço de marketplace/assinatura.
}
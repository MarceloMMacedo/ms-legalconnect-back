package br.com.legalconnect.advogado.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.AreaAtuacao;
import br.com.legalconnect.advogado.dto.response.AreaAtuacaoResponseDTO;
import br.com.legalconnect.advogado.mapper.AreaAtuacaoMapper;
import br.com.legalconnect.advogado.repository.AreaAtuacaoRepository;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;

/**
 * Serviço para gestão de Áreas de Atuação (dados mestre).
 * Essas entidades são consideradas globais ou de um tenant específico da
 * plataforma,
 * mas para este módulo, o acesso é simplificado como dados mestre.
 */
@Service
public class AreaAtuacaoService {

    private final AreaAtuacaoRepository areaAtuacaoRepository;
    private final AreaAtuacaoMapper areaAtuacaoMapper;

    @Autowired
    public AreaAtuacaoService(AreaAtuacaoRepository areaAtuacaoRepository, AreaAtuacaoMapper areaAtuacaoMapper) {
        this.areaAtuacaoRepository = areaAtuacaoRepository;
        this.areaAtuacaoMapper = areaAtuacaoMapper;
    }

    /**
     * Busca uma Área de Atuação pelo ID.
     *
     * @param id ID da Área de Atuação.
     * @return DTO da Área de Atuação.
     * @throws BusinessException se a Área de Atuação não for encontrada.
     */
    public AreaAtuacaoResponseDTO findAreaAtuacaoById(UUID id) {
        AreaAtuacao areaAtuacao = areaAtuacaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Área de Atuação não encontrada."));
        return areaAtuacaoMapper.toResponseDTO(areaAtuacao);
    }

    /**
     * Lista todas as Áreas de Atuação.
     *
     * @return Lista de DTOs de Áreas de Atuação.
     */
    public List<AreaAtuacaoResponseDTO> findAllAreasAtuacao() {
        return areaAtuacaoRepository.findAll().stream()
                .map(areaAtuacaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Métodos para criar, atualizar e deletar podem ser adicionados
    // se essas entidades mestre forem gerenciáveis via API
    // Por exemplo, por um ADMIN da plataforma.
    // Ex:
    /*
     * @Transactional
     * public AreaAtuacaoResponseDTO createAreaAtuacao(AreaAtuacaoRequestDTO
     * requestDTO) {
     * if (areaAtuacaoRepository.findByNome(requestDTO.getNome()).isPresent()) {
     * throw new BusinessException(ErrorCode.DADOS_INVALIDOS, HttpStatus.CONFLICT,
     * "Área de Atuação com este nome já existe.");
     * }
     * AreaAtuacao areaAtuacao = areaAtuacaoMapper.toEntity(requestDTO);
     * areaAtuacao = areaAtuacaoRepository.save(areaAtuacao);
     * return areaAtuacaoMapper.toResponseDTO(areaAtuacao);
     * }
     * 
     * @Transactional
     * public AreaAtuacaoResponseDTO updateAreaAtuacao(UUID id,
     * AreaAtuacaoRequestDTO requestDTO) {
     * AreaAtuacao existing = areaAtuacaoRepository.findById(id)
     * .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
     * HttpStatus.NOT_FOUND, "Área de Atuação não encontrada."));
     * if (!existing.getNome().equalsIgnoreCase(requestDTO.getNome()) &&
     * areaAtuacaoRepository.findByNome(requestDTO.getNome()).isPresent()) {
     * throw new BusinessException(ErrorCode.DADOS_INVALIDOS, HttpStatus.CONFLICT,
     * "Área de Atuação com este nome já existe.");
     * }
     * areaAtuacaoMapper.updateEntityFromDto(requestDTO, existing); // Supondo um
     * updateEntityFromDto no mapper
     * existing = areaAtuacaoRepository.save(existing);
     * return areaAtuacaoMapper.toResponseDTO(existing);
     * }
     * 
     * @Transactional
     * public void deleteAreaAtuacao(UUID id) {
     * if (!areaAtuacaoRepository.existsById(id)) {
     * throw new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
     * HttpStatus.NOT_FOUND, "Área de Atuação não encontrada para deleção.");
     * }
     * // Regra de Negócio: Verificar se está em uso por algum Profissional antes de
     * deletar
     * // if (profissionalRepository.existsByAreaAtuacaoId(id)) { ... }
     * areaAtuacaoRepository.deleteById(id);
     * }
     */
}
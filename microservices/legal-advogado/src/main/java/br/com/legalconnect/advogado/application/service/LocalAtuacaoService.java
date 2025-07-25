package br.com.legalconnect.advogado.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.application.dto.response.LocalAtuacaoResponseDTO;
import br.com.legalconnect.advogado.application.mapper.LocalAtuacaoMapper;
import br.com.legalconnect.advogado.domain.modal.entity.LocalAtuacao;
import br.com.legalconnect.advogado.domain.repository.LocalAtuacaoRepository;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;

/**
 * Serviço para gestão de Locais de Atuação (dados mestre).
 */
@Service
public class LocalAtuacaoService {

    private final LocalAtuacaoRepository localAtuacaoRepository;
    private final LocalAtuacaoMapper localAtuacaoMapper;

    @Autowired
    public LocalAtuacaoService(LocalAtuacaoRepository localAtuacaoRepository, LocalAtuacaoMapper localAtuacaoMapper) {
        this.localAtuacaoRepository = localAtuacaoRepository;
        this.localAtuacaoMapper = localAtuacaoMapper;
    }

    /**
     * Busca um Local de Atuação pelo ID.
     *
     * @param id ID do Local de Atuação.
     * @return DTO do Local de Atuação.
     * @throws BusinessException se o Local de Atuação não for encontrado.
     */
    public LocalAtuacaoResponseDTO findLocalAtuacaoById(UUID id) {
        LocalAtuacao localAtuacao = localAtuacaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Local de Atuação não encontrado."));
        return localAtuacaoMapper.toResponseDTO(localAtuacao);
    }

    /**
     * Lista todos os Locais de Atuação.
     *
     * @return Lista de DTOs de Locais de Atuação.
     */
    public List<LocalAtuacaoResponseDTO> findAllLocaisAtuacao() {
        return localAtuacaoRepository.findAll().stream()
                .map(localAtuacaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
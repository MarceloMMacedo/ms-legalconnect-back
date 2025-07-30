package br.com.legalconnect.advogado.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.TipoAtendimento;
import br.com.legalconnect.advogado.dto.response.TipoAtendimentoResponseDTO;
import br.com.legalconnect.advogado.mapper.TipoAtendimentoMapper;
import br.com.legalconnect.advogado.repository.TipoAtendimentoRepository;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;

/**
 * Serviço para gestão de Tipos de Atendimento (dados mestre).
 */
@Service
public class TipoAtendimentoService {

    private final TipoAtendimentoRepository tipoAtendimentoRepository;
    private final TipoAtendimentoMapper tipoAtendimentoMapper;

    @Autowired
    public TipoAtendimentoService(TipoAtendimentoRepository tipoAtendimentoRepository,
            TipoAtendimentoMapper tipoAtendimentoMapper) {
        this.tipoAtendimentoRepository = tipoAtendimentoRepository;
        this.tipoAtendimentoMapper = tipoAtendimentoMapper;
    }

    /**
     * Busca um Tipo de Atendimento pelo ID.
     *
     * @param id ID do Tipo de Atendimento.
     * @return DTO do Tipo de Atendimento.
     * @throws BusinessException se o Tipo de Atendimento não for encontrado.
     */
    public TipoAtendimentoResponseDTO findTipoAtendimentoById(UUID id) {
        TipoAtendimento tipoAtendimento = tipoAtendimentoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Tipo de Atendimento não encontrado."));
        return tipoAtendimentoMapper.toResponseDTO(tipoAtendimento);
    }

    /**
     * Lista todos os Tipos de Atendimento.
     *
     * @return Lista de DTOs de Tipos de Atendimento.
     */
    public List<TipoAtendimentoResponseDTO> findAllTiposAtendimento() {
        return tipoAtendimentoRepository.findAll().stream()
                .map(tipoAtendimentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
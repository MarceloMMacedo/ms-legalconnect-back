package br.com.legalconnect.advogado.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.Idioma;
import br.com.legalconnect.advogado.dto.response.IdiomaResponseDTO;
import br.com.legalconnect.advogado.mapper.IdiomaMapper;
import br.com.legalconnect.advogado.repository.IdiomaRepository;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;

/**
 * Serviço para gestão de Idiomas (dados mestre).
 */
@Service
public class IdiomaService {

    private final IdiomaRepository idiomaRepository;
    private final IdiomaMapper idiomaMapper;

    @Autowired
    public IdiomaService(IdiomaRepository idiomaRepository, IdiomaMapper idiomaMapper) {
        this.idiomaRepository = idiomaRepository;
        this.idiomaMapper = idiomaMapper;
    }

    /**
     * Busca um Idioma pelo ID.
     *
     * @param id ID do Idioma.
     * @return DTO do Idioma.
     * @throws BusinessException se o Idioma não for encontrado.
     */
    public IdiomaResponseDTO findIdiomaById(UUID id) {
        Idioma idioma = idiomaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Idioma não encontrado."));
        return idiomaMapper.toResponseDTO(idioma);
    }

    /**
     * Lista todos os Idiomas.
     *
     * @return Lista de DTOs de Idiomas.
     */
    public List<IdiomaResponseDTO> findAllIdiomas() {
        return idiomaRepository.findAll().stream()
                .map(idiomaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
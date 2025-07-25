package br.com.legalconnect.advogado.application.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.application.dto.request.CertificacaoRequestDTO;
import br.com.legalconnect.advogado.application.dto.response.CertificacaoResponseDTO;
import br.com.legalconnect.advogado.application.mapper.CertificacaoMapper;
import br.com.legalconnect.advogado.domain.modal.entity.Certificacao;
import br.com.legalconnect.advogado.domain.modal.entity.Profissional;
import br.com.legalconnect.advogado.domain.repository.CertificacaoRepository;
import br.com.legalconnect.advogado.domain.repository.ProfissionalRepository;
import br.com.legalconnect.commom.service.TenantContext;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;

/**
 * Serviço responsável pela gestão das certificações de um Profissional.
 * Este serviço é granular e pode ser usado para operações diretas em
 * certificações,
 * embora o ProfissionalService orquestre a maioria.
 */
@Service
public class CertificacaoService {

    private final CertificacaoRepository certificacaoRepository;
    private final CertificacaoMapper certificacaoMapper;
    private final ProfissionalRepository profissionalRepository;

    @Autowired
    public CertificacaoService(CertificacaoRepository certificacaoRepository,
            CertificacaoMapper certificacaoMapper,
            ProfissionalRepository profissionalRepository) {
        this.certificacaoRepository = certificacaoRepository;
        this.certificacaoMapper = certificacaoMapper;
        this.profissionalRepository = profissionalRepository;
    }

    /**
     * Cria uma nova certificação para um profissional específico.
     * Regras de Negócio:
     * - O profissional deve existir e pertencer ao tenant atual.
     * - A certificação é associada ao profissional e ao tenant.
     *
     * @param profissionalId ID do profissional.
     * @param requestDTO     DTO com os dados da certificação.
     * @return DTO da certificação criada.
     */
    @Transactional
    public CertificacaoResponseDTO createCertificacao(UUID profissionalId, CertificacaoRequestDTO requestDTO) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado."));

        if (!profissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        Certificacao certificacao = certificacaoMapper.toEntity(requestDTO);
        certificacao.setProfissional(profissional);
        certificacao.setTenantId(tenantId);

        certificacao = certificacaoRepository.save(certificacao); // profissionalId é passado para o
                                                                  // save no repositório customizado
        return certificacaoMapper.toResponseDTO(certificacao);
    }

    /**
     * Atualiza uma certificação existente de um profissional.
     * Regras de Negócio:
     * - A certificação deve existir e pertencer ao profissional e tenant corretos.
     *
     * @param profissionalId ID do profissional.
     * @param certificacaoId ID da certificação.
     * @param requestDTO     DTO com os dados para atualização.
     * @return DTO da certificação atualizada.
     */
    @Transactional
    public CertificacaoResponseDTO updateCertificacao(UUID profissionalId, UUID certificacaoId,
            CertificacaoRequestDTO requestDTO) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Certificacao existingCertificacao = certificacaoRepository
                .findByIdAndProfissionalId(certificacaoId, profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Certificação não encontrada para este profissional."));

        if (!existingCertificacao.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Certificação pertence a outro tenant ou profissional.");
        }

        certificacaoMapper.updateEntityFromDto(requestDTO, existingCertificacao);
        existingCertificacao = certificacaoRepository.save(existingCertificacao);
        return certificacaoMapper.toResponseDTO(existingCertificacao);
    }

    /**
     * Busca uma certificação pelo ID do profissional e da certificação.
     *
     * @param profissionalId ID do profissional.
     * @param certificacaoId ID da certificação.
     * @return DTO da certificação.
     */
    public CertificacaoResponseDTO findCertificacaoById(UUID profissionalId, UUID certificacaoId) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Certificacao certificacao = certificacaoRepository.findByIdAndProfissionalId(certificacaoId, profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Certificação não encontrada para este profissional."));

        if (!certificacao.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Certificação pertence a outro tenant.");
        }

        return certificacaoMapper.toResponseDTO(certificacao);
    }

    /**
     * Lista todas as certificações de um profissional.
     *
     * @param profissionalId ID do profissional.
     * @return Lista de DTOs de certificações.
     */
    public List<CertificacaoResponseDTO> findAllCertificacoesByProfissionalId(UUID profissionalId) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        // Verificar se o profissional existe e pertence ao tenant
        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado."));
        if (!profissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        return certificacaoRepository.findAllByProfissionalId(profissionalId).stream()
                .map(certificacaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deleta uma certificação de um profissional.
     *
     * @param profissionalId ID do profissional.
     * @param certificacaoId ID da certificação a ser deletada.
     */
    @Transactional
    public void deleteCertificacao(UUID profissionalId, UUID certificacaoId) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        // Verificar se a certificação existe e pertence ao profissional e tenant
        Certificacao existingCertificacao = certificacaoRepository
                .findByIdAndProfissionalId(certificacaoId, profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Certificação não encontrada para deleção."));

        if (!existingCertificacao.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Certificação pertence a outro tenant.");
        }

        certificacaoRepository.deleteByIdAndProfissionalId(certificacaoId, profissionalId);
    }
}
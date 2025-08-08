package br.com.legalconnect.advogado.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.FormacaoAcademica;
import br.com.legalconnect.advogado.domain.Profissional;
import br.com.legalconnect.advogado.dto.request.FormacaoAcademicaRequestDTO;
import br.com.legalconnect.advogado.dto.response.FormacaoAcademicaResponseDTO;
import br.com.legalconnect.advogado.mapper.FormacaoAcademicaMapper;
import br.com.legalconnect.advogado.repository.FormacaoRepository;
import br.com.legalconnect.advogado.repository.ProfissionalRepository;
import br.com.legalconnect.commom.service.TenantContext;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;

/**
 * Serviço responsável pela gestão das formações acadêmicas de um Profissional.
 */
@Service
public class FormacaoAcademicaService {

        private final FormacaoRepository formacaoRepository;
        private final FormacaoAcademicaMapper formacaoAcademicaMapper;
        private final ProfissionalRepository profissionalRepository;

        @Autowired
        public FormacaoAcademicaService(FormacaoRepository formacaoRepository,
                        FormacaoAcademicaMapper formacaoAcademicaMapper,
                        ProfissionalRepository profissionalRepository) {
                this.formacaoRepository = formacaoRepository;
                this.formacaoAcademicaMapper = formacaoAcademicaMapper;
                this.profissionalRepository = profissionalRepository;
        }

        /**
         * Cria uma nova formação acadêmica para um profissional específico.
         * Regras de Negócio:
         * - O profissional deve existir e pertencer ao tenant atual.
         * - A formação é associada ao profissional e ao tenant.
         *
         * @param profissionalId ID do profissional.
         * @param requestDTO     DTO com os dados da formação.
         * @return DTO da formação criada.
         */
        @Transactional
        public FormacaoAcademicaResponseDTO createFormacaoAcademica(UUID profissionalId,
                        FormacaoAcademicaRequestDTO requestDTO) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                Profissional profissional = profissionalRepository.findById(profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO,
                                                HttpStatus.NOT_FOUND,
                                                "Profissional não encontrado."));

                if (!profissional.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Profissional pertence a outro tenant.");
                }

                FormacaoAcademica formacao = formacaoAcademicaMapper.toEntity(requestDTO);
                formacao.setProfissional(profissional);
                formacao.setTenantId(tenantId);

                formacao = formacaoRepository.save(formacao);
                return formacaoAcademicaMapper.toResponseDTO(formacao);
        }

        /**
         * Atualiza uma formação acadêmica existente de um profissional.
         * Regras de Negócio:
         * - A formação deve existir e pertencer ao profissional e tenant corretos.
         *
         * @param profissionalId ID do profissional.
         * @param formacaoId     ID da formação.
         * @param requestDTO     DTO com os dados para atualização.
         * @return DTO da formação atualizada.
         */
        @Transactional
        public FormacaoAcademicaResponseDTO updateFormacaoAcademica(UUID profissionalId, UUID formacaoId,
                        FormacaoAcademicaRequestDTO requestDTO) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                FormacaoAcademica existingFormacao = formacaoRepository
                                .findByIdAndProfissionalId(formacaoId, profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                                                HttpStatus.NOT_FOUND,
                                                "Formação acadêmica não encontrada para este profissional."));

                if (!existingFormacao.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Formação pertence a outro tenant ou profissional.");
                }

                formacaoAcademicaMapper.updateEntityFromDto(requestDTO, existingFormacao);
                existingFormacao = formacaoRepository.save(existingFormacao);
                return formacaoAcademicaMapper.toResponseDTO(existingFormacao);
        }

        /**
         * Busca uma formação acadêmica pelo ID do profissional e da formação.
         *
         * @param profissionalId ID do profissional.
         * @param formacaoId     ID da formação.
         * @return DTO da formação.
         */
        public FormacaoAcademicaResponseDTO findFormacaoAcademicaById(UUID profissionalId, UUID formacaoId) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                FormacaoAcademica formacao = formacaoRepository.findByIdAndProfissionalId(formacaoId, profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                                                HttpStatus.NOT_FOUND,
                                                "Formação acadêmica não encontrada para este profissional."));

                if (!formacao.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Formação pertence a outro tenant.");
                }

                return formacaoAcademicaMapper.toResponseDTO(formacao);
        }

        /**
         * Lista todas as formações acadêmicas de um profissional.
         *
         * @param profissionalId ID do profissional.
         * @return Lista de DTOs de formações.
         */
        public List<FormacaoAcademicaResponseDTO> findAllFormacoesAcademicasByProfissionalId(UUID profissionalId) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                Profissional profissional = profissionalRepository.findById(profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO,
                                                HttpStatus.NOT_FOUND,
                                                "Profissional não encontrado."));
                if (!profissional.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Profissional pertence a outro tenant.");
                }

                return formacaoRepository.findAllByProfissionalId(profissionalId).stream()
                                .map(formacaoAcademicaMapper::toResponseDTO)
                                .collect(Collectors.toList());
        }

        /**
         * Deleta uma formação acadêmica de um profissional.
         *
         * @param profissionalId ID do profissional.
         * @param formacaoId     ID da formação a ser deletada.
         */
        @Transactional
        public void deleteFormacaoAcademica(UUID profissionalId, UUID formacaoId) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                FormacaoAcademica existingFormacao = formacaoRepository
                                .findByIdAndProfissionalId(formacaoId, profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                                                HttpStatus.NOT_FOUND,
                                                "Formação acadêmica não encontrada para deleção."));

                if (!existingFormacao.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Formação pertence a outro tenant.");
                }

                formacaoRepository.deleteByIdAndProfissionalId(formacaoId, profissionalId);
        }
}
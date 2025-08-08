package br.com.legalconnect.advogado.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.ExperienciaProfissional;
import br.com.legalconnect.advogado.domain.Profissional;
import br.com.legalconnect.advogado.dto.request.ExperienciaProfissionalRequestDTO;
import br.com.legalconnect.advogado.dto.response.ExperienciaProfissionalResponseDTO;
import br.com.legalconnect.advogado.mapper.ExperienciaProfissionalMapper;
import br.com.legalconnect.advogado.repository.ExperienciaRepository;
import br.com.legalconnect.advogado.repository.ProfissionalRepository;
import br.com.legalconnect.commom.service.TenantContext;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;

/**
 * Serviço responsável pela gestão das experiências profissionais de um
 * Profissional.
 */
@Service
public class ExperienciaProfissionalService {

        private final ExperienciaRepository experienciaRepository;
        private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
        private final ProfissionalRepository profissionalRepository;

        @Autowired
        public ExperienciaProfissionalService(ExperienciaRepository experienciaRepository,
                        ExperienciaProfissionalMapper experienciaProfissionalMapper,
                        ProfissionalRepository profissionalRepository) {
                this.experienciaRepository = experienciaRepository;
                this.experienciaProfissionalMapper = experienciaProfissionalMapper;
                this.profissionalRepository = profissionalRepository;
        }

        /**
         * Cria uma nova experiência profissional para um profissional específico.
         * Regras de Negócio:
         * - O profissional deve existir e pertencer ao tenant atual.
         * - A experiência é associada ao profissional e ao tenant.
         *
         * @param profissionalId ID do profissional.
         * @param requestDTO     DTO com os dados da experiência.
         * @return DTO da experiência criada.
         */
        @Transactional
        public ExperienciaProfissionalResponseDTO createExperienciaProfissional(UUID profissionalId,
                        ExperienciaProfissionalRequestDTO requestDTO) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                Profissional profissional = profissionalRepository.findById(profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO,
                                                HttpStatus.NOT_FOUND,
                                                "Profissional não encontrado."));

                if (!profissional.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Profissional pertence a outro tenant.");
                }

                ExperienciaProfissional experiencia = experienciaProfissionalMapper.toEntity(requestDTO);
                experiencia.setProfissional(profissional);
                experiencia.setTenantId(tenantId);

                experiencia = experienciaRepository.save(experiencia);
                return experienciaProfissionalMapper.toResponseDTO(experiencia);
        }

        /**
         * Atualiza uma experiência profissional existente de um profissional.
         * Regras de Negócio:
         * - A experiência deve existir e pertencer ao profissional e tenant corretos.
         *
         * @param profissionalId ID do profissional.
         * @param experienciaId  ID da experiência.
         * @param requestDTO     DTO com os dados para atualização.
         * @return DTO da experiência atualizada.
         */
        @Transactional
        public ExperienciaProfissionalResponseDTO updateExperienciaProfissional(UUID profissionalId, UUID experienciaId,
                        ExperienciaProfissionalRequestDTO requestDTO) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                ExperienciaProfissional existingExperiencia = experienciaRepository
                                .findByIdAndProfissionalId(experienciaId, profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                                                HttpStatus.NOT_FOUND,
                                                "Experiência profissional não encontrada para este profissional."));

                if (!existingExperiencia.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Experiência pertence a outro tenant ou profissional.");
                }

                experienciaProfissionalMapper.updateEntityFromDto(requestDTO, existingExperiencia);
                existingExperiencia = experienciaRepository.save(existingExperiencia);
                return experienciaProfissionalMapper.toResponseDTO(existingExperiencia);
        }

        /**
         * Busca uma experiência profissional pelo ID do profissional e da experiência.
         *
         * @param profissionalId ID do profissional.
         * @param experienciaId  ID da experiência.
         * @return DTO da experiência.
         */
        public ExperienciaProfissionalResponseDTO findExperienciaProfissionalById(UUID profissionalId,
                        UUID experienciaId) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                ExperienciaProfissional experiencia = experienciaRepository
                                .findByIdAndProfissionalId(experienciaId, profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                                                HttpStatus.NOT_FOUND,
                                                "Experiência profissional não encontrada para este profissional."));

                if (!experiencia.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Experiência pertence a outro tenant.");
                }

                return experienciaProfissionalMapper.toResponseDTO(experiencia);
        }

        /**
         * Lista todas as experiências profissionais de um profissional.
         *
         * @param profissionalId ID do profissional.
         * @return Lista de DTOs de experiências.
         */
        public List<ExperienciaProfissionalResponseDTO> findAllExperienciasProfissionaisByProfissionalId(
                        UUID profissionalId) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                Profissional profissional = profissionalRepository.findById(profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO,
                                                HttpStatus.NOT_FOUND,
                                                "Profissional não encontrado."));
                if (!profissional.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Profissional pertence a outro tenant.");
                }

                return experienciaRepository.findAllByProfissionalId(profissionalId).stream()
                                .map(experienciaProfissionalMapper::toResponseDTO)
                                .collect(Collectors.toList());
        }

        /**
         * Deleta uma experiência profissional de um profissional.
         *
         * @param profissionalId ID do profissional.
         * @param experienciaId  ID da experiência a ser deletada.
         */
        @Transactional
        public void deleteExperienciaProfissional(UUID profissionalId, UUID experienciaId) {
                UUID tenantId = TenantContext.getCurrentTenantId();

                ExperienciaProfissional existingExperiencia = experienciaRepository
                                .findByIdAndProfissionalId(experienciaId, profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA,
                                                HttpStatus.NOT_FOUND,
                                                "Experiência profissional não encontrada para deleção."));

                if (!existingExperiencia.getTenantId().equals(tenantId)) {
                        throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                                        "Acesso negado. Experiência pertence a outro tenant.");
                }

                experienciaRepository.deleteByIdAndProfissionalId(experienciaId, profissionalId);
        }
}
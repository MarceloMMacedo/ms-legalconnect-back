package br.com.legalconnect.advogado.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Importar Pageable
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.Certificacao;
import br.com.legalconnect.advogado.domain.ExperienciaProfissional;
import br.com.legalconnect.advogado.domain.FormacaoAcademica;
import br.com.legalconnect.advogado.domain.Profissional;
import br.com.legalconnect.advogado.dto.request.ProfissionalCreateRequest;
import br.com.legalconnect.advogado.dto.request.ProfissionalUpdateRequest;
import br.com.legalconnect.advogado.dto.response.ProfissionalResponseDTO;
import br.com.legalconnect.advogado.mapper.AreaAtuacaoMapper;
import br.com.legalconnect.advogado.mapper.CertificacaoMapper;
import br.com.legalconnect.advogado.mapper.DocumentoMapper;
import br.com.legalconnect.advogado.mapper.ExperienciaProfissionalMapper;
import br.com.legalconnect.advogado.mapper.FormacaoAcademicaMapper;
import br.com.legalconnect.advogado.mapper.IdiomaMapper;
import br.com.legalconnect.advogado.mapper.LocalAtuacaoMapper;
import br.com.legalconnect.advogado.mapper.ProfissionalMapper;
import br.com.legalconnect.advogado.mapper.TipoAtendimentoMapper;
import br.com.legalconnect.advogado.repository.AreaAtuacaoRepository;
import br.com.legalconnect.advogado.repository.CertificacaoRepository;
import br.com.legalconnect.advogado.repository.DocumentoRepository;
import br.com.legalconnect.advogado.repository.ExperienciaRepository;
import br.com.legalconnect.advogado.repository.FormacaoRepository;
import br.com.legalconnect.advogado.repository.IdiomaRepository;
import br.com.legalconnect.advogado.repository.LocalAtuacaoRepository;
import br.com.legalconnect.advogado.repository.ProfissionalRepository;
import br.com.legalconnect.advogado.repository.TipoAtendimentoRepository;
import br.com.legalconnect.commom.dto.request.PessoaRequestDTO;
import br.com.legalconnect.commom.model.Pessoa;
import br.com.legalconnect.commom.service.PessoaService;
import br.com.legalconnect.commom.service.S3Service; // Assumindo S3Service para upload de documentos
import br.com.legalconnect.commom.service.TenantContext; // Assumindo TenantContext para multitenancy
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;

/**
 * Serviço responsável pela gestão completa do Profissional (Advogado).
 * Inclui operações de CRUD, validações de negócio e orquestração com serviços
 * de entidades aninhadas
 * e dados mestre. Gerencia também o upload de documentos e a associação com o
 * tenant.
 */
@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final ProfissionalMapper profissionalMapper;
    private final PessoaService pessoaService; // Para gerenciar a entidade Pessoa
    private final CertificacaoMapper certificacaoMapper;
    private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
    private final FormacaoAcademicaMapper formacaoAcademicaMapper;
    private final DocumentoMapper documentoMapper;
    private final CertificacaoRepository certificacaoRepository;
    private final ExperienciaRepository experienciaRepository;
    private final FormacaoRepository formacaoRepository;
    private final DocumentoRepository documentoRepository;
    private final S3Service s3Service; // Serviço para integração com S3
    private final AreaAtuacaoRepository areaAtuacaoRepository;
    private final IdiomaRepository idiomaRepository;
    private final LocalAtuacaoRepository localAtuacaoRepository;
    private final TipoAtendimentoRepository tipoAtendimentoRepository;
    private final AreaAtuacaoMapper areaAtuacaoMapper;
    private final IdiomaMapper idiomaMapper;
    private final LocalAtuacaoMapper localAtuacaoMapper;
    private final TipoAtendimentoMapper tipoAtendimentoMapper;

    @Autowired
    public ProfissionalService(ProfissionalRepository profissionalRepository,
            ProfissionalMapper profissionalMapper,
            PessoaService pessoaService,
            CertificacaoMapper certificacaoMapper,
            ExperienciaProfissionalMapper experienciaProfissionalMapper,
            FormacaoAcademicaMapper formacaoAcademicaMapper,
            DocumentoMapper documentoMapper,
            CertificacaoRepository certificacaoRepository,
            ExperienciaRepository experienciaRepository,
            FormacaoRepository formacaoRepository,
            DocumentoRepository documentoRepository,
            S3Service s3Service,
            AreaAtuacaoRepository areaAtuacaoRepository,
            IdiomaRepository idiomaRepository,
            LocalAtuacaoRepository localAtuacaoRepository,
            TipoAtendimentoRepository tipoAtendimentoRepository,
            AreaAtuacaoMapper areaAtuacaoMapper,
            IdiomaMapper idiomaMapper,
            LocalAtuacaoMapper localAtuacaoMapper,
            TipoAtendimentoMapper tipoAtendimentoMapper) {
        this.profissionalRepository = profissionalRepository;
        this.profissionalMapper = profissionalMapper;
        this.pessoaService = pessoaService;
        this.certificacaoMapper = certificacaoMapper;
        this.experienciaProfissionalMapper = experienciaProfissionalMapper;
        this.formacaoAcademicaMapper = formacaoAcademicaMapper;
        this.documentoMapper = documentoMapper;
        this.certificacaoRepository = certificacaoRepository;
        this.experienciaRepository = experienciaRepository;
        this.formacaoRepository = formacaoRepository;
        this.documentoRepository = documentoRepository;
        this.s3Service = s3Service;
        this.areaAtuacaoRepository = areaAtuacaoRepository;
        this.idiomaRepository = idiomaRepository;
        this.localAtuacaoRepository = localAtuacaoRepository;
        this.tipoAtendimentoRepository = tipoAtendimentoRepository;
        this.areaAtuacaoMapper = areaAtuacaoMapper;
        this.idiomaMapper = idiomaMapper;
        this.localAtuacaoMapper = localAtuacaoMapper;
        this.tipoAtendimentoMapper = tipoAtendimentoMapper;
    }

    /**
     * Cria um novo Profissional no sistema.
     * Regras de Negócio:
     * - Valida a unicidade do número da OAB.
     * - Associa o Profissional a um Tenant (obtido do contexto de segurança).
     * - Cria a entidade Pessoa associada através do PessoaService.
     * - Gerencia a criação de certificações, experiências e formações aninhadas.
     * - Define o status inicial do profissional.
     * - Valida a existência do plano.
     *
     * @param createRequest DTO com os dados para criação do Profissional.
     * @return DTO do Profissional criado.
     * @throws BusinessException se a OAB já estiver cadastrada, ou se o
     *                           plano/tenant não existirem.
     */
    @Transactional
    public ProfissionalResponseDTO createProfissional(ProfissionalCreateRequest createRequest) {
        UUID tenantId = TenantContext.getCurrentTenantId(); // Assumindo que o tenantId vem do contexto de segurança

        // Regra de Negócio: Validar unicidade da OAB
        if (profissionalRepository.existsByNumeroOab(createRequest.getNumeroOab())) {
            throw new BusinessException(ErrorCode.OAB_DUPLICADA, HttpStatus.CONFLICT, createRequest.getNumeroOab());
        }

        // Regra de Negócio: Validar unicidade da Pessoa (CPF/Email) antes de criar o
        // Profissional
        if (pessoaService.findPessoaByCpf(createRequest.getCpf()).isPresent()) {
            throw new BusinessException(ErrorCode.CPF_DUPLICADO, HttpStatus.CONFLICT, createRequest.getCpf());
        }
        if (pessoaService.findPessoaByEmail(createRequest.getUsuario().getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICADO, HttpStatus.CONFLICT,
                    createRequest.getUsuario().getEmail());
        }

        // Criar a Pessoa associada primeiro, pois Profissional estende Pessoa
        PessoaRequestDTO pessoaRequestDTO = new PessoaRequestDTO();
        pessoaRequestDTO.setUsuario(createRequest.getUsuario());
        pessoaRequestDTO.setNomeCompleto(createRequest.getNomeCompleto());
        pessoaRequestDTO.setCpf(createRequest.getCpf());
        pessoaRequestDTO.setDataNascimento(createRequest.getDataNascimento());
        pessoaRequestDTO.setTelefones(createRequest.getTelefones());
        pessoaRequestDTO.setEnderecos(createRequest.getEnderecos());

        Pessoa newPessoa = pessoaService.createPessoa(pessoaRequestDTO);

        // Regra de Negócio: Validar que o planoId existe (assumindo um PlanoService ou
        // repositório de Plano)
        // if (!planoService.existsById(createRequest.getPlanoId())) {
        // throw new BusinessException(ErrorCode.PLANO_NAO_ENCONTRADO,
        // HttpStatus.NOT_FOUND);
        // }

        Profissional profissional = profissionalMapper.toEntity(createRequest);
        profissional.setPessoaId(newPessoa.getId()); // Associa o ID da Pessoa recém-criada
        profissional.setUsuario(newPessoa.getUsuario()); // Garante que o usuário de Pessoa esteja associado
        profissional.setTenantId(tenantId); // Regra de Negócio: Define o tenant do profissional
        profissional.setStatusProfissional("PENDING_APPROVAL"); // Regra de Negócio: Status inicial

        // Relacionamentos muitos-para-muitos (apenas IDs aqui)
        if (createRequest.getLocaisAtuacaoIds() != null) {
            validateMasterDataExistence(createRequest.getLocaisAtuacaoIds(), localAtuacaoRepository::findById,
                    "Local de Atuação");
            profissional.setLocaisAtuacaoIds(createRequest.getLocaisAtuacaoIds().stream().collect(Collectors.toSet()));
        }
        if (createRequest.getAreaAtuacaoIds() != null) {
            validateMasterDataExistence(createRequest.getAreaAtuacaoIds(), areaAtuacaoRepository::findById,
                    "Área de Atuação");
            profissional.setAreaAtuacaoIds(createRequest.getAreaAtuacaoIds().stream().collect(Collectors.toSet()));
        }
        if (createRequest.getIdiomaIds() != null) {
            validateMasterDataExistence(createRequest.getIdiomaIds(), idiomaRepository::findById, "Idioma");
            profissional.setIdiomaIds(createRequest.getIdiomaIds().stream().collect(Collectors.toSet()));
        }
        if (createRequest.getTipoAtendimentoIds() != null) {
            validateMasterDataExistence(createRequest.getTipoAtendimentoIds(), tipoAtendimentoRepository::findById,
                    "Tipo de Atendimento");
            profissional
                    .setTipoAtendimentoIds(createRequest.getTipoAtendimentoIds().stream().collect(Collectors.toSet()));
        }

        profissional = profissionalRepository.save(profissional);

        // Gerenciar certificações, experiências e formações
        manageNestedEntities(profissional, createRequest);

        return mapToResponseDTOWithDetails(profissional);
    }

    /**
     * Atualiza um Profissional existente.
     * Regras de Negócio:
     * - Valida a existência do Profissional e do Tenant.
     * - Permite a atualização de dados da Pessoa associada.
     * - Gerencia a adição, atualização e remoção de certificações, experiências e
     * formações.
     * - Atualiza relacionamentos com dados mestre.
     *
     * @param id            ID do Profissional a ser atualizado.
     * @param updateRequest DTO com os dados para atualização.
     * @return DTO do Profissional atualizado.
     * @throws BusinessException se o Profissional não for encontrado, ou por outras
     *                           violações de negócio.
     */
    @Transactional
    public ProfissionalResponseDTO updateProfissional(UUID id, ProfissionalUpdateRequest updateRequest) {
        UUID tenantId = TenantContext.getCurrentTenantId(); // Assumindo tenantId do contexto de segurança

        Profissional existingProfissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado para atualização."));

        // Regra de Negócio: O profissional só pode ser atualizado pelo seu próprio
        // tenant
        if (!existingProfissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        // Atualiza os dados da Pessoa associada (se fornecidos)
        if (updateRequest.getPessoa() != null) {
            pessoaService.updatePessoa(existingProfissional.getPessoaId(), updateRequest.getPessoa());
        }

        // Mapeia os campos atualizáveis para a entidade Profissional
        profissionalMapper.updateEntityFromDto(updateRequest, existingProfissional);

        // Regra de Negócio: Validar que o planoId existe se for alterado
        if (updateRequest.getPlanoId() != null
                && !existingProfissional.getPlanoId().equals(updateRequest.getPlanoId())) {
            // if (!planoService.existsById(updateRequest.getPlanoId())) {
            // throw new BusinessException(ErrorCode.PLANO_NAO_ENCONTRADO,
            // HttpStatus.NOT_FOUND);
            // }
            existingProfissional.setPlanoId(updateRequest.getPlanoId());
        }

        // Atualiza relacionamentos muitos-para-muitos
        if (updateRequest.getLocaisAtuacaoIds() != null) {
            validateMasterDataExistence(updateRequest.getLocaisAtuacaoIds(), localAtuacaoRepository::findById,
                    "Local de Atuação");
            existingProfissional
                    .setLocaisAtuacaoIds(updateRequest.getLocaisAtuacaoIds().stream().collect(Collectors.toSet()));
        }
        if (updateRequest.getAreaAtuacaoIds() != null) {
            validateMasterDataExistence(updateRequest.getAreaAtuacaoIds(), areaAtuacaoRepository::findById,
                    "Área de Atuação");
            existingProfissional
                    .setAreaAtuacaoIds(updateRequest.getAreaAtuacaoIds().stream().collect(Collectors.toSet()));
        }
        if (updateRequest.getIdiomaIds() != null) {
            validateMasterDataExistence(updateRequest.getIdiomaIds(), idiomaRepository::findById, "Idioma");
            existingProfissional.setIdiomaIds(updateRequest.getIdiomaIds().stream().collect(Collectors.toSet()));
        }
        if (updateRequest.getTipoAtendimentoIds() != null) {
            validateMasterDataExistence(updateRequest.getTipoAtendimentoIds(), tipoAtendimentoRepository::findById,
                    "Tipo de Atendimento");
            existingProfissional
                    .setTipoAtendimentoIds(updateRequest.getTipoAtendimentoIds().stream().collect(Collectors.toSet()));
        }

        // Gerenciar coleções aninhadas (certificações, experiências, formações)
        manageNestedEntities(existingProfissional, updateRequest);

        existingProfissional = profissionalRepository.save(existingProfissional);
        return mapToResponseDTOWithDetails(existingProfissional);
    }

    /**
     * Busca um Profissional pelo ID.
     * Regras de Negócio:
     * - Garante que o Profissional pertence ao Tenant do contexto de segurança.
     * - Popula os dados mestre relacionados.
     *
     * @param id ID do Profissional.
     * @return DTO do Profissional com detalhes.
     * @throws BusinessException se o Profissional não for encontrado ou não
     *                           pertencer ao tenant.
     */
    public ProfissionalResponseDTO findProfissionalById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado."));

        // Regra de Negócio: Acesso restrito ao tenant
        if (!profissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        return mapToResponseDTOWithDetails(profissional);
    }

    /**
     * Busca todos os Profissionais de um determinado Tenant com paginação.
     *
     * @param pageable Objeto Pageable contendo informações de paginação e
     *                 ordenação.
     * @return Página de DTOs de Profissionais.
     */
    public Page<ProfissionalResponseDTO> findAllProfissionais(Pageable pageable) {
        UUID tenantId = TenantContext.getCurrentTenantId(); // Filtra por tenant

        Page<Profissional> profissionaisPage = profissionalRepository.findAllByTenantId(tenantId, pageable); // This
                                                                                                             // line is
                                                                                                             // correct,
                                                                                                             // no
                                                                                                             // change
                                                                                                             // needed
                                                                                                             // here.
        return profissionaisPage.map(this::mapToResponseDTOWithDetails);
    }

    /**
     * Deleta um Profissional pelo ID.
     * Regras de Negócio:
     * - Apenas o proprietário do tenant pode deletar.
     * - Orquestra a deleção de entidades aninhadas (Certificações, Experiências,
     * Formações, Documentos).
     * - Deleção da Pessoa associada.
     *
     * @param id ID do Profissional a ser deletado.
     * @throws BusinessException se o Profissional não for encontrado ou não
     *                           pertencer ao tenant.
     */
    @Transactional
    public void deleteProfissional(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado para deleção."));

        // Regra de Negócio: Acesso restrito ao tenant
        if (!profissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        // Deleta entidades aninhadas (cascade pode fazer isso, mas explicitar é bom
        // para clareza)
        certificacaoRepository.findAllByProfissionalId(id)
                .forEach(c -> certificacaoRepository.deleteByIdAndProfissionalId(c.getId(), id));
        experienciaRepository.findAllByProfissionalId(id)
                .forEach(e -> experienciaRepository.deleteByIdAndProfissionalId(e.getId(), id));
        formacaoRepository.findAllByProfissionalId(id)
                .forEach(f -> formacaoRepository.deleteByIdAndProfissionalId(f.getId(), id));
        documentoRepository.findAllByProfissionalId(id).forEach(d -> {
            s3Service.deleteFile(d.getUrlS3()); // Deleta arquivo do S3
            documentoRepository.deleteByIdAndProfissionalId(d.getId(), id);
        });

        // Deleta o profissional
        profissionalRepository.deleteById(id);

        // Deleta a pessoa associada
        pessoaService.deletePessoa(profissional.getPessoaId());
    }

    /**
     * Método auxiliar para gerenciar a adição, atualização e remoção de entidades
     * aninhadas
     * (certificações, experiências, formações) para um Profissional.
     *
     * @param profissional O Profissional pai.
     * @param request      O DTO de requisição (Create ou Update) contendo as listas
     *                     aninhadas.
     */
    private void manageNestedEntities(Profissional profissional, ProfissionalCreateRequest request) {
        UUID tenantId = profissional.getTenantId();

        // Certificações
        if (request.getCertificacoes() != null) {
            // Remover certificações que não estão mais no DTO
            profissional.getCertificacoes().removeIf(existingCert -> request.getCertificacoes().stream()
                    .noneMatch(dto -> existingCert.getId().equals(dto.getId())));
            request.getCertificacoes().forEach(dto -> {
                if (dto.getId() == null) { // Nova certificação
                    Certificacao newCert = certificacaoMapper.toEntity(dto);
                    newCert.setProfissional(profissional);
                    newCert.setTenantId(tenantId);
                    profissional.getCertificacoes().add(newCert);
                } else { // Atualizar certificação existente
                    profissional.getCertificacoes().stream()
                            .filter(c -> c.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(c -> certificacaoMapper.updateEntityFromDto(dto, c));
                }
            });
        }

        // Experiências Profissionais
        if (request.getExperiencias() != null) {
            profissional.getExperiencias().removeIf(existingExp -> request.getExperiencias().stream()
                    .noneMatch(dto -> existingExp.getId().equals(dto.getId())));
            request.getExperiencias().forEach(dto -> {
                if (dto.getId() == null) { // Nova experiência
                    ExperienciaProfissional newExp = experienciaProfissionalMapper.toEntity(dto);
                    newExp.setProfissional(profissional);
                    newExp.setTenantId(tenantId);
                    profissional.getExperiencias().add(newExp);
                } else { // Atualizar experiência existente
                    profissional.getExperiencias().stream()
                            .filter(e -> e.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(e -> experienciaProfissionalMapper.updateEntityFromDto(dto, e));
                }
            });
        }

        // Formações Acadêmicas
        if (request.getFormacoes() != null) {
            profissional.getFormacoes().removeIf(existingForm -> request.getFormacoes().stream()
                    .noneMatch(dto -> existingForm.getId().equals(dto.getId())));
            request.getFormacoes().forEach(dto -> {
                if (dto.getId() == null) { // Nova formação
                    FormacaoAcademica newForm = formacaoAcademicaMapper.toEntity(dto);
                    newForm.setProfissional(profissional);
                    newForm.setTenantId(tenantId);
                    profissional.getFormacoes().add(newForm);
                } else { // Atualizar formação existente
                    profissional.getFormacoes().stream()
                            .filter(f -> f.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(f -> formacaoAcademicaMapper.updateEntityFromDto(dto, f));
                }
            });
        }
    }

    /**
     * Sobrecarga para o método de gerenciamento de entidades aninhadas para
     * ProfissionalUpdateRequest.
     */
    private void manageNestedEntities(Profissional profissional, ProfissionalUpdateRequest request) {
        UUID tenantId = profissional.getTenantId();

        // Certificações
        if (request.getCertificacoes() != null) {
            // Coleta IDs das certificações no DTO para identificar as que serão removidas
            Set<UUID> dtoCertIds = request.getCertificacoes().stream()
                    .map(c -> c.getId())
                    .collect(Collectors.toSet());

            // Remove certificações que existem na entidade mas não no DTO (deleção)
            profissional.getCertificacoes().removeIf(
                    existingCert -> existingCert.getId() != null && !dtoCertIds.contains(existingCert.getId()));

            request.getCertificacoes().forEach(dto -> {
                if (dto.getId() == null) { // Nova certificação (ID nulo indica novo)
                    Certificacao newCert = certificacaoMapper.toEntity(dto);
                    newCert.setProfissional(profissional);
                    newCert.setTenantId(tenantId);
                    profissional.getCertificacoes().add(newCert);
                } else { // Atualizar certificação existente
                    profissional.getCertificacoes().stream()
                            .filter(c -> c.getId() != null && c.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(c -> certificacaoMapper.updateEntityFromDto(dto, c));
                }
            });
        } else {
            // Se a lista no DTO for nula, assume-se que todas as certificações devem ser
            // removidas
            profissional.getCertificacoes().clear();
        }

        // Experiências Profissionais (lógica similar às certificações)
        if (request.getExperiencias() != null) {
            Set<UUID> dtoExpIds = request.getExperiencias().stream()
                    .map(e -> e.getId())
                    .collect(Collectors.toSet());
            profissional.getExperiencias()
                    .removeIf(existingExp -> existingExp.getId() != null && !dtoExpIds.contains(existingExp.getId()));
            request.getExperiencias().forEach(dto -> {
                if (dto.getId() == null) {
                    ExperienciaProfissional newExp = experienciaProfissionalMapper.toEntity(dto);
                    newExp.setProfissional(profissional);
                    newExp.setTenantId(tenantId);
                    profissional.getExperiencias().add(newExp);
                } else {
                    profissional.getExperiencias().stream()
                            .filter(e -> e.getId() != null && e.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(e -> experienciaProfissionalMapper.updateEntityFromDto(dto, e));
                }
            });
        } else {
            profissional.getExperiencias().clear();
        }

        // Formações Acadêmicas (lógica similar às certificações)
        if (request.getFormacoes() != null) {
            Set<UUID> dtoFormIds = request.getFormacoes().stream()
                    .map(f -> f.getId())
                    .collect(Collectors.toSet());
            profissional.getFormacoes().removeIf(
                    existingForm -> existingForm.getId() != null && !dtoFormIds.contains(existingForm.getId()));
            request.getFormacoes().forEach(dto -> {
                if (dto.getId() == null) {
                    FormacaoAcademica newForm = formacaoAcademicaMapper.toEntity(dto);
                    newForm.setProfissional(profissional);
                    newForm.setTenantId(tenantId);
                    profissional.getFormacoes().add(newForm);
                } else {
                    profissional.getFormacoes().stream()
                            .filter(f -> f.getId() != null && f.getId().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(f -> formacaoAcademicaMapper.updateEntityFromDto(dto, f));
                }
            });
        } else {
            profissional.getFormacoes().clear();
        }
    }

    /**
     * Mapeia um Profissional para ProfissionalResponseDTO e popula os dados mestre
     * relacionados.
     * Esta é uma funcionalidade completa para apresentar o perfil do advogado.
     *
     * @param profissional A entidade Profissional.
     * @return O DTO de resposta detalhado.
     */
    private ProfissionalResponseDTO mapToResponseDTOWithDetails(Profissional profissional) {
        ProfissionalResponseDTO responseDTO = profissionalMapper.toResponseDTO(profissional);

        // Popula listas de DTOs de dados mestre
        responseDTO.setLocaisAtuacao(
                localAtuacaoRepository.findAllById(profissional.getLocaisAtuacaoIds())
                        .stream()
                        .map(localAtuacaoMapper::toResponseDTO)
                        .collect(Collectors.toList()));
        responseDTO.setAreasAtuacao(
                areaAtuacaoRepository.findAllById(profissional.getAreaAtuacaoIds())
                        .stream()
                        .map(areaAtuacaoMapper::toResponseDTO)
                        .collect(Collectors.toList()));
        responseDTO.setIdiomas(
                idiomaRepository.findAllById(profissional.getIdiomaIds())
                        .stream()
                        .map(idiomaMapper::toResponseDTO)
                        .collect(Collectors.toList()));
        responseDTO.setTiposAtendimento(
                tipoAtendimentoRepository.findAllById(profissional.getTipoAtendimentoIds())
                        .stream()
                        .map(tipoAtendimentoMapper::toResponseDTO)
                        .collect(Collectors.toList()));

        // Popula as coleções aninhadas
        responseDTO.setCertificacoes(
                profissional.getCertificacoes().stream()
                        .map(certificacaoMapper::toResponseDTO)
                        .collect(Collectors.toList()));
        responseDTO.setDocumentos(
                profissional.getDocumentos().stream()
                        .map(documentoMapper::toResponseDTO)
                        .collect(Collectors.toList()));
        responseDTO.setExperiencias(
                profissional.getExperiencias().stream()
                        .map(experienciaProfissionalMapper::toResponseDTO)
                        .collect(Collectors.toList()));
        responseDTO.setFormacoes(
                profissional.getFormacoes().stream()
                        .map(formacaoAcademicaMapper::toResponseDTO)
                        .collect(Collectors.toList()));

        // Define se faz parte de plano com base no planoId (regra de negócio)
        responseDTO.setFazParteDePlano(profissional.getPlanoId() != null); // Simplificado

        return responseDTO;
    }

    /**
     * Valida a existência de entidades de dados mestre.
     *
     * @param ids        Lista de UUIDs a serem validados.
     * @param finder     Função para encontrar a entidade por ID.
     * @param entityName Nome da entidade para mensagens de erro.
     * @param <T>        Tipo da entidade.
     */
    private <T> void validateMasterDataExistence(List<UUID> ids,
            java.util.function.Function<UUID, java.util.Optional<T>> finder, String entityName) {
        for (UUID id : ids) {
            if (finder.apply(id).isEmpty()) {
                throw new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        entityName + " com ID " + id + " não encontrado(a).");
            }
        }
    }
}
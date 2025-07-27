package br.com.legalconnect.advogado.controller;

import static br.com.legalconnect.enums.StatusResponse.SUCESSO;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.advogado.application.dto.request.CertificacaoRequestDTO;
import br.com.legalconnect.advogado.application.dto.request.DocumentoUploadRequest;
import br.com.legalconnect.advogado.application.dto.request.ExperienciaProfissionalRequestDTO;
import br.com.legalconnect.advogado.application.dto.request.FormacaoAcademicaRequestDTO;
import br.com.legalconnect.advogado.application.dto.request.ProfissionalCreateRequest;
import br.com.legalconnect.advogado.application.dto.request.ProfissionalUpdateRequest;
import br.com.legalconnect.advogado.application.dto.response.CertificacaoResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.DocumentoResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.ExperienciaProfissionalResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.FormacaoAcademicaResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.ProfissionalResponseDTO;
import br.com.legalconnect.advogado.application.service.CertificacaoService;
import br.com.legalconnect.advogado.application.service.DocumentoService;
import br.com.legalconnect.advogado.application.service.ExperienciaProfissionalService;
import br.com.legalconnect.advogado.application.service.FormacaoAcademicaService;
import br.com.legalconnect.advogado.application.service.ProfissionalService;
import br.com.legalconnect.commom.dto.request.UserRequestDTO;
import br.com.legalconnect.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller para o módulo de Profissionais (Advogados).
 * Gerencia as operações REST para perfis de advogados,
 * incluindo suas certificações, experiências, formações e documentos.
 */
@RestController
@RequestMapping("/api/v1/advogados/profissionais")
@Tag(name = "Profissionais", description = "Gerenciamento de perfis de Advogados")
public class ProfissionalController {

        private final ProfissionalService profissionalService;
        private final CertificacaoService certificacaoService;
        private final ExperienciaProfissionalService experienciaProfissionalService;
        private final FormacaoAcademicaService formacaoAcademicaService;
        private final DocumentoService documentoService;

        @Autowired
        public ProfissionalController(ProfissionalService profissionalService,
                        CertificacaoService certificacaoService,
                        ExperienciaProfissionalService experienciaProfissionalService,
                        FormacaoAcademicaService formacaoAcademicaService,
                        DocumentoService documentoService) {
                this.profissionalService = profissionalService;
                this.certificacaoService = certificacaoService;
                this.experienciaProfissionalService = experienciaProfissionalService;
                this.formacaoAcademicaService = formacaoAcademicaService;
                this.documentoService = documentoService;
        }

        /**
         * Cria um novo profissional (advogado) no sistema.
         * Funcionalidade Completa: Cadastro de Advogado com dados aninhados.
         * Regras de Negócio: Validação de unicidade de OAB, CPF e Email. Associação a
         * um tenant.
         *
         * @param request      DTO com os dados do profissional a ser criado.
         * @param userIdHeader Opcional. ID do usuário logado/solicitante, vindo do
         *                     cabeçalho X-Correlation-Id.
         * @return ResponseEntity com o DTO do profissional criado.
         */
        @Operation(summary = "Cria um novo profissional", description = "Registra um novo advogado com seus dados pessoais e informações profissionais.", responses = {
                        @ApiResponse(responseCode = "201", description = "Profissional criado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "409", description = "OAB, CPF ou Email já cadastrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping
        public ResponseEntity<BaseResponse<ProfissionalResponseDTO>> createProfissional(
                        @Valid @RequestBody ProfissionalCreateRequest request,
                        @RequestHeader(value = "X-Correlation-Id", required = false) String userIdHeader) {
                if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
                        try {
                                UUID userUuid = UUID.fromString(userIdHeader); // Validar se é um UUID válido

                                if (request.getUsuario() == null) {
                                        // Se o DTO de usuário não foi fornecido no corpo, cria um com o ID do header
                                        request.setUsuario(UserRequestDTO.builder().id(userUuid.toString()).build());
                                } else if (request.getUsuario().getId() == null
                                                || request.getUsuario().getId().trim().isEmpty()) {
                                        // Se o DTO de usuário foi fornecido, mas sem ID, usa o do header
                                        request.getUsuario().setId(userUuid.toString());
                                }
                        } catch (IllegalArgumentException e) {
                                // Logar ou tratar erro se userIdHeader não for um UUID válido
                                // Para este caso, vamos apenas logar e ignorar o userIdHeader inválido.
                                System.err.println("X-Correlation-Id inválido: " + userIdHeader
                                                + ". Ignorando ID do cabeçalho.");
                        }
                }

                ProfissionalResponseDTO response = profissionalService.createProfissional(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.<ProfissionalResponseDTO>builder()
                                                .status(SUCESSO)
                                                .message("Profissional criado com sucesso.")
                                                .data(response)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /**
         * Atualiza um profissional existente.
         * Funcionalidade Completa: Atualização de perfil de advogado, incluindo dados
         * da Pessoa e coleções aninhadas.
         * Regras de Negócio: Acesso restrito ao próprio tenant, validação de
         * existência.
         *
         * @param id      ID do profissional a ser atualizado.
         * @param request DTO com os dados de atualização.
         * @return ResponseEntity com o DTO do profissional atualizado.
         */
        @Operation(summary = "Atualiza um profissional existente", description = "Atualiza os dados de um advogado, incluindo informações pessoais e coleções relacionadas.", responses = {
                        @ApiResponse(responseCode = "200", description = "Profissional atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "409", description = "Conflito de dados (ex: email duplicado)"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PutMapping("/{id}")
        public ResponseEntity<BaseResponse<ProfissionalResponseDTO>> updateProfissional(
                        @Parameter(description = "ID do profissional a ser atualizado") @PathVariable UUID id,
                        @Valid @RequestBody ProfissionalUpdateRequest request) {
                ProfissionalResponseDTO response = profissionalService.updateProfissional(id, request);
                return ResponseEntity.ok(BaseResponse.<ProfissionalResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Profissional atualizado com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Busca um profissional pelo ID.
         * Funcionalidade Completa: Exibição detalhada do perfil de um advogado.
         * Regras de Negócio: Acesso restrito ao próprio tenant.
         *
         * @param id ID do profissional.
         * @return ResponseEntity com o DTO do profissional.
         */
        @Operation(summary = "Busca um profissional pelo ID", description = "Retorna os detalhes completos de um advogado específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Profissional encontrado com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<ProfissionalResponseDTO>> getProfissionalById(
                        @Parameter(description = "ID do profissional") @PathVariable UUID id) {
                ProfissionalResponseDTO response = profissionalService.findProfissionalById(id);
                return ResponseEntity.ok(BaseResponse.<ProfissionalResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Profissional encontrado com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todos os profissionais do tenant atual com paginação.
         * Funcionalidade Completa: Listagem paginada de advogados para o marketplace ou
         * para gestão interna do tenant.
         * Regras de Negócio: Filtro automático por tenant_id.
         *
         * @param page Número da página (0-indexed).
         * @param size Tamanho da página.
         * @param sort Critério de ordenação (ex: campo,asc ou campo,desc).
         * @return ResponseEntity com a página de DTOs de profissionais.
         */
        @Operation(summary = "Lista todos os profissionais com paginação", description = "Retorna uma lista paginada de todos os advogados associados ao tenant atual.", responses = {
                        @ApiResponse(responseCode = "200", description = "Profissionais listados com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping
        public ResponseEntity<BaseResponse<Page<ProfissionalResponseDTO>>> getAllProfissionais(
                        @Parameter(description = "Número da página (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Critério de ordenação (ex: nomeCompleto,asc ou numeroOab,desc)", example = "nomeCompleto,asc") @RequestParam(defaultValue = "nomeCompleto,asc") String[] sort) {

                Sort sortCriteria = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
                PageRequest pageable = PageRequest.of(page, size, sortCriteria);

                Page<ProfissionalResponseDTO> response = profissionalService.findAllProfissionais(pageable);
                return ResponseEntity.ok(BaseResponse.<Page<ProfissionalResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Profissionais listados com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Deleta um profissional pelo ID.
         * Funcionalidade Completa: Remoção completa do perfil do advogado e dados
         * associados.
         * Regras de Negócio: Acesso restrito ao próprio tenant. Deleção em cascata
         * (certificações, experiências, documentos, formação, pessoa).
         *
         * @param id ID do profissional a ser deletado.
         * @return ResponseEntity de sucesso sem conteúdo.
         */
        @Operation(summary = "Deleta um profissional pelo ID", description = "Remove um advogado e todos os seus dados associados do sistema.", responses = {
                        @ApiResponse(responseCode = "200", description = "Profissional deletado com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<BaseResponse<Void>> deleteProfissional(
                        @Parameter(description = "ID do profissional a ser deletado") @PathVariable UUID id) {
                profissionalService.deleteProfissional(id);
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .status(SUCESSO)
                                .message("Profissional deletado com sucesso.")
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        // --- Endpoints para Certificações ---

        /**
         * Adiciona uma certificação a um profissional.
         * Funcionalidade Completa: Gerenciamento granular de certificações.
         * Regras de Negócio: Certificação associada ao profissional e ao tenant.
         *
         * @param profissionalId ID do profissional.
         * @param requestDTO     DTO da certificação.
         * @return ResponseEntity com a certificação criada.
         */
        @Operation(summary = "Adiciona uma certificação a um profissional", description = "Adiciona uma nova certificação ao perfil de um advogado.", responses = {
                        @ApiResponse(responseCode = "201", description = "Certificação adicionada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping("/{profissionalId}/certificacoes")
        public ResponseEntity<BaseResponse<CertificacaoResponseDTO>> addCertificacao(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Valid @RequestBody CertificacaoRequestDTO requestDTO) {
                CertificacaoResponseDTO response = certificacaoService.createCertificacao(profissionalId, requestDTO);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.<CertificacaoResponseDTO>builder()
                                                .status(SUCESSO)
                                                .message("Certificação adicionada com sucesso.")
                                                .data(response)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /**
         * Atualiza uma certificação de um profissional.
         * Funcionalidade Completa: Atualização de detalhes de uma certificação
         * existente.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param certificacaoId ID da certificação.
         * @param requestDTO     DTO com os dados de atualização.
         * @return ResponseEntity com a certificação atualizada.
         */
        @Operation(summary = "Atualiza uma certificação de um profissional", description = "Atualiza os detalhes de uma certificação existente associada a um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Certificação atualizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (certificação pertence a outro tenant ou profissional)"),
                        @ApiResponse(responseCode = "404", description = "Certificação não encontrada para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PutMapping("/{profissionalId}/certificacoes/{certificacaoId}")
        public ResponseEntity<BaseResponse<CertificacaoResponseDTO>> updateCertificacao(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da certificação a ser atualizada") @PathVariable UUID certificacaoId,
                        @Valid @RequestBody CertificacaoRequestDTO requestDTO) {
                CertificacaoResponseDTO response = certificacaoService.updateCertificacao(profissionalId,
                                certificacaoId,
                                requestDTO);
                return ResponseEntity.ok(BaseResponse.<CertificacaoResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Certificação atualizada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Busca uma certificação específica de um profissional.
         * Funcionalidade Completa: Consulta individual de certificação.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param certificacaoId ID da certificação.
         * @return ResponseEntity com a certificação encontrada.
         */
        @Operation(summary = "Busca uma certificação específica de um profissional", description = "Retorna os detalhes de uma certificação específica de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Certificação encontrada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (certificação pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Certificação não encontrada para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/certificacoes/{certificacaoId}")
        public ResponseEntity<BaseResponse<CertificacaoResponseDTO>> getCertificacaoById(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da certificação") @PathVariable UUID certificacaoId) {
                CertificacaoResponseDTO response = certificacaoService.findCertificacaoById(profissionalId,
                                certificacaoId);
                return ResponseEntity.ok(BaseResponse.<CertificacaoResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Certificação encontrada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todas as certificações de um profissional.
         * Funcionalidade Completa: Visualização de todas as certificações de um
         * advogado.
         * Regras de Negócio: Acesso restrito ao próprio tenant.
         *
         * @param profissionalId ID do profissional.
         * @return ResponseEntity com a lista de certificações.
         */
        @Operation(summary = "Lista todas as certificações de um profissional", description = "Retorna uma lista de todas as certificações associadas a um advogado específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Certificações listadas com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/certificacoes")
        public ResponseEntity<BaseResponse<List<CertificacaoResponseDTO>>> getAllCertificacoes(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId) {
                List<CertificacaoResponseDTO> response = certificacaoService
                                .findAllCertificacoesByProfissionalId(profissionalId);
                return ResponseEntity.ok(BaseResponse.<List<CertificacaoResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Certificações listadas com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Deleta uma certificação de um profissional.
         * Funcionalidade Completa: Remoção de uma certificação específica.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param certificacaoId ID da certificação.
         * @return ResponseEntity de sucesso sem conteúdo.
         */
        @Operation(summary = "Deleta uma certificação de um profissional", description = "Remove uma certificação específica do perfil de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Certificação deletada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (certificação pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Certificação não encontrada para deleção"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @DeleteMapping("/{profissionalId}/certificacoes/{certificacaoId}")
        public ResponseEntity<BaseResponse<Void>> deleteCertificacao(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da certificação a ser deletada") @PathVariable UUID certificacaoId) {
                certificacaoService.deleteCertificacao(profissionalId, certificacaoId);
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .status(SUCESSO)
                                .message("Certificação deletada com sucesso.")
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        // --- Endpoints para Experiências Profissionais ---

        /**
         * Adiciona uma experiência profissional a um profissional.
         * Funcionalidade Completa: Gerenciamento granular de experiências.
         * Regras de Negócio: Experiência associada ao profissional e ao tenant.
         *
         * @param profissionalId ID do profissional.
         * @param requestDTO     DTO da experiência.
         * @return ResponseEntity com a experiência criada.
         */
        @Operation(summary = "Adiciona uma experiência profissional a um profissional", description = "Adiciona uma nova experiência profissional ao perfil de um advogado.", responses = {
                        @ApiResponse(responseCode = "201", description = "Experiência profissional adicionada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping("/{profissionalId}/experiencias")
        public ResponseEntity<BaseResponse<ExperienciaProfissionalResponseDTO>> addExperienciaProfissional(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Valid @RequestBody ExperienciaProfissionalRequestDTO requestDTO) {
                ExperienciaProfissionalResponseDTO response = experienciaProfissionalService
                                .createExperienciaProfissional(profissionalId, requestDTO);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.<ExperienciaProfissionalResponseDTO>builder()
                                                .status(SUCESSO)
                                                .message("Experiência profissional adicionada com sucesso.")
                                                .data(response)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /**
         * Atualiza uma experiência profissional de um profissional.
         * Funcionalidade Completa: Atualização de detalhes de uma experiência
         * existente.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param experienciaId  ID da experiência.
         * @param requestDTO     DTO com os dados de atualização.
         * @return ResponseEntity com a experiência atualizada.
         */
        @Operation(summary = "Atualiza uma experiência profissional de um profissional", description = "Atualiza os detalhes de uma experiência profissional existente associada a um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Experiência profissional atualizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (experiência pertence a outro tenant ou profissional)"),
                        @ApiResponse(responseCode = "404", description = "Experiência profissional não encontrada para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PutMapping("/{profissionalId}/experiencias/{experienciaId}")
        public ResponseEntity<BaseResponse<ExperienciaProfissionalResponseDTO>> updateExperienciaProfissional(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da experiência a ser atualizada") @PathVariable UUID experienciaId,
                        @Valid @RequestBody ExperienciaProfissionalRequestDTO requestDTO) {
                ExperienciaProfissionalResponseDTO response = experienciaProfissionalService
                                .updateExperienciaProfissional(profissionalId, experienciaId, requestDTO);
                return ResponseEntity.ok(BaseResponse.<ExperienciaProfissionalResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Experiência profissional atualizada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Busca uma experiência profissional específica de um profissional.
         * Funcionalidade Completa: Consulta individual de experiência.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param experienciaId  ID da experiência.
         * @return ResponseEntity com a experiência encontrada.
         */
        @Operation(summary = "Busca uma experiência profissional específica de um profissional", description = "Retorna os detalhes de uma experiência profissional específica de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Experiência profissional encontrada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (experiência pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Experiência profissional não encontrada para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/experiencias/{experienciaId}")
        public ResponseEntity<BaseResponse<ExperienciaProfissionalResponseDTO>> getExperienciaProfissionalById(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da experiência") @PathVariable UUID experienciaId) {
                ExperienciaProfissionalResponseDTO response = experienciaProfissionalService
                                .findExperienciaProfissionalById(profissionalId, experienciaId);
                return ResponseEntity.ok(BaseResponse.<ExperienciaProfissionalResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Experiência profissional encontrada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todas as experiências profissionais de um profissional.
         * Funcionalidade Completa: Visualização de todas as experiências de um
         * advogado.
         * Regras de Negócio: Acesso restrito ao próprio tenant.
         *
         * @param profissionalId ID do profissional.
         * @return ResponseEntity com a lista de experiências.
         */
        @Operation(summary = "Lista todas as experiências profissionais de um profissional", description = "Retorna uma lista de todas as experiências profissionais associadas a um advogado específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Experiências profissionais listadas com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/experiencias")
        public ResponseEntity<BaseResponse<List<ExperienciaProfissionalResponseDTO>>> getAllExperienciasProfissionais(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId) {
                List<ExperienciaProfissionalResponseDTO> response = experienciaProfissionalService
                                .findAllExperienciasProfissionaisByProfissionalId(profissionalId);
                return ResponseEntity.ok(BaseResponse.<List<ExperienciaProfissionalResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Experiências profissionais listadas com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Deleta uma experiência profissional de um profissional.
         * Funcionalidade Completa: Remoção de uma experiência específica.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param experienciaId  ID da experiência.
         * @return ResponseEntity de sucesso sem conteúdo.
         */
        @Operation(summary = "Deleta uma experiência profissional de um profissional", description = "Remove uma experiência profissional específica do perfil de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Experiência profissional deletada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (experiência pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Experiência profissional não encontrada para deleção"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @DeleteMapping("/{profissionalId}/experiencias/{experienciaId}")
        public ResponseEntity<BaseResponse<Void>> deleteExperienciaProfissional(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da experiência a ser deletada") @PathVariable UUID experienciaId) {
                experienciaProfissionalService.deleteExperienciaProfissional(profissionalId, experienciaId);
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .status(SUCESSO)
                                .message("Experiência profissional deletada com sucesso.")
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        // --- Endpoints para Formações Acadêmicas ---

        /**
         * Adiciona uma formação acadêmica a um profissional.
         * Funcionalidade Completa: Gerenciamento granular de formações.
         * Regras de Negócio: Formação associada ao profissional e ao tenant.
         *
         * @param profissionalId ID do profissional.
         * @param requestDTO     DTO da formação.
         * @return ResponseEntity com a formação criada.
         */
        @Operation(summary = "Adiciona uma formação acadêmica a um profissional", description = "Adiciona uma nova formação acadêmica ao perfil de um advogado.", responses = {
                        @ApiResponse(responseCode = "201", description = "Formação acadêmica adicionada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping("/{profissionalId}/formacoes")
        public ResponseEntity<BaseResponse<FormacaoAcademicaResponseDTO>> addFormacaoAcademica(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Valid @RequestBody FormacaoAcademicaRequestDTO requestDTO) {
                FormacaoAcademicaResponseDTO response = formacaoAcademicaService.createFormacaoAcademica(profissionalId,
                                requestDTO);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.<FormacaoAcademicaResponseDTO>builder()
                                                .status(SUCESSO)
                                                .message("Formação acadêmica adicionada com sucesso.")
                                                .data(response)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /**
         * Atualiza uma formação acadêmica de um profissional.
         * Funcionalidade Completa: Atualização de detalhes de uma formação existente.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param formacaoId     ID da formação.
         * @param requestDTO     DTO com os dados de atualização.
         * @return ResponseEntity com a formação atualizada.
         */
        @Operation(summary = "Atualiza uma formação acadêmica de um profissional", description = "Atualiza os detalhes de uma formação acadêmica existente associada a um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Formação acadêmica atualizada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (formação pertence a outro tenant ou profissional)"),
                        @ApiResponse(responseCode = "404", description = "Formação acadêmica não encontrada para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PutMapping("/{profissionalId}/formacoes/{formacaoId}")
        public ResponseEntity<BaseResponse<FormacaoAcademicaResponseDTO>> updateFormacaoAcademica(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da formação a ser atualizada") @PathVariable UUID formacaoId,
                        @Valid @RequestBody FormacaoAcademicaRequestDTO requestDTO) {
                FormacaoAcademicaResponseDTO response = formacaoAcademicaService.updateFormacaoAcademica(profissionalId,
                                formacaoId, requestDTO);
                return ResponseEntity.ok(BaseResponse.<FormacaoAcademicaResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Formação acadêmica atualizada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Busca uma formação acadêmica específica de um profissional.
         * Funcionalidade Completa: Consulta individual de formação.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param formacaoId     ID da formação.
         * @return ResponseEntity com a formação encontrada.
         */
        @Operation(summary = "Busca uma formação acadêmica específica de um profissional", description = "Retorna os detalhes de uma formação acadêmica específica de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Formação acadêmica encontrada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (formação pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Formação acadêmica não encontrada para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/formacoes/{formacaoId}")
        public ResponseEntity<BaseResponse<FormacaoAcademicaResponseDTO>> getFormacaoAcademicaById(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da formação") @PathVariable UUID formacaoId) {
                FormacaoAcademicaResponseDTO response = formacaoAcademicaService.findFormacaoAcademicaById(
                                profissionalId,
                                formacaoId);
                return ResponseEntity.ok(BaseResponse.<FormacaoAcademicaResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Formação acadêmica encontrada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todas as formações acadêmicas de um profissional.
         * Funcionalidade Completa: Visualização de todas as formações de um advogado.
         * Regras de Negócio: Acesso restrito ao próprio tenant.
         *
         * @param profissionalId ID do profissional.
         * @return ResponseEntity com a lista de formações.
         */
        @Operation(summary = "Lista todas as formações acadêmicas de um profissional", description = "Retorna uma lista de todas as formações acadêmicas associadas a um advogado específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Formações acadêmicas listadas com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/formacoes")
        public ResponseEntity<BaseResponse<List<FormacaoAcademicaResponseDTO>>> getAllFormacoesAcademicas(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId) {
                List<FormacaoAcademicaResponseDTO> response = formacaoAcademicaService
                                .findAllFormacoesAcademicasByProfissionalId(profissionalId);
                return ResponseEntity.ok(BaseResponse.<List<FormacaoAcademicaResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Formações acadêmicas listadas com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Deleta uma formação acadêmica de um profissional.
         * Funcionalidade Completa: Remoção de uma formação específica.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param formacaoId     ID da formação.
         * @return ResponseEntity de sucesso sem conteúdo.
         */
        @Operation(summary = "Deleta uma formação acadêmica de um profissional", description = "Remove uma formação acadêmica específica do perfil de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Formação acadêmica deletada com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (formação pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Formação acadêmica não encontrada para deleção"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @DeleteMapping("/{profissionalId}/formacoes/{formacaoId}")
        public ResponseEntity<BaseResponse<Void>> deleteFormacaoAcademica(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID da formação a ser deletada") @PathVariable UUID formacaoId) {
                formacaoAcademicaService.deleteFormacaoAcademica(profissionalId, formacaoId);
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .status(SUCESSO)
                                .message("Formação acadêmica deletada com sucesso.")
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        // --- Endpoints para Documentos ---

        /**
         * Realiza o upload de um documento para um profissional.
         * Funcionalidade Completa: Armazenamento de documentos no S3 e metadados no DB.
         * Regras de Negócio: Associação ao profissional e tenant, validação de formato
         * Base64.
         *
         * @param profissionalId ID do profissional.
         * @param request        DTO com os dados do documento e o arquivo em Base64.
         * @return ResponseEntity com o DTO do documento criado.
         */
        @Operation(summary = "Realiza o upload de um documento para um profissional", description = "Faz o upload de um documento para o S3 e persiste seus metadados para um advogado.", responses = {
                        @ApiResponse(responseCode = "201", description = "Documento enviado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos (ex: Base64 inválido)"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Falha ao fazer upload do documento ou erro interno do servidor")
        })
        @PostMapping("/{profissionalId}/documentos/upload")
        public ResponseEntity<BaseResponse<DocumentoResponseDTO>> uploadDocumento(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Valid @RequestBody DocumentoUploadRequest request) {
                DocumentoResponseDTO response = documentoService.uploadDocumento(profissionalId, request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.<DocumentoResponseDTO>builder()
                                                .status(SUCESSO)
                                                .message("Documento enviado com sucesso.")
                                                .data(response)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /**
         * Busca um documento específico de um profissional.
         * Funcionalidade Completa: Consulta individual de documento.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param documentoId    ID do documento.
         * @return ResponseEntity com o DTO do documento encontrado.
         */
        @Operation(summary = "Busca um documento específico de um profissional", description = "Retorna os detalhes de um documento específico de um advogado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Documento encontrado com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (documento pertence a outro tenant ou profissional)"),
                        @ApiResponse(responseCode = "404", description = "Documento não encontrado para este profissional"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/documentos/{documentoId}")
        public ResponseEntity<BaseResponse<DocumentoResponseDTO>> getDocumentoById(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID do documento") @PathVariable UUID documentoId) {
                DocumentoResponseDTO response = documentoService.findDocumentoById(profissionalId, documentoId);
                return ResponseEntity.ok(BaseResponse.<DocumentoResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Documento encontrado com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todos os documentos de um profissional.
         * Funcionalidade Completa: Visualização de todos os documentos de um advogado.
         * Regras de Negócio: Acesso restrito ao próprio tenant.
         *
         * @param profissionalId ID do profissional.
         * @return ResponseEntity com a lista de documentos.
         */
        @Operation(summary = "Lista todos os documentos de um profissional", description = "Retorna uma lista de todos os documentos associados a um advogado específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Documentos listados com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (profissional pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{profissionalId}/documentos")
        public ResponseEntity<BaseResponse<List<DocumentoResponseDTO>>> getAllDocumentos(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId) {
                List<DocumentoResponseDTO> response = documentoService
                                .findAllDocumentosByProfissionalId(profissionalId);
                return ResponseEntity.ok(BaseResponse.<List<DocumentoResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Documentos listados com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Deleta um documento de um profissional.
         * Funcionalidade Completa: Remoção do documento do S3 e do banco de dados.
         * Regras de Negócio: Validação de pertencimento ao profissional e tenant.
         *
         * @param profissionalId ID do profissional.
         * @param documentoId    ID do documento.
         * @return ResponseEntity de sucesso sem conteúdo.
         */
        @Operation(summary = "Deleta um documento de um profissional", description = "Remove um documento específico do perfil de um advogado, incluindo a remoção do S3.", responses = {
                        @ApiResponse(responseCode = "200", description = "Documento deletado com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso negado (documento pertence a outro tenant)"),
                        @ApiResponse(responseCode = "404", description = "Documento não encontrado para deleção"),
                        @ApiResponse(responseCode = "500", description = "Falha ao deletar o documento do S3 ou erro interno do servidor")
        })
        @DeleteMapping("/{profissionalId}/documentos/{documentoId}")
        public ResponseEntity<BaseResponse<Void>> deleteDocumento(
                        @Parameter(description = "ID do profissional") @PathVariable UUID profissionalId,
                        @Parameter(description = "ID do documento a ser deletado") @PathVariable UUID documentoId) {
                documentoService.deleteDocumento(profissionalId, documentoId);
                return ResponseEntity.ok(BaseResponse.<Void>builder()
                                .status(SUCESSO)
                                .message("Documento deletado com sucesso.")
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }
}
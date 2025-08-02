package br.com.legalconnect.depoimento.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping; // Adicionada a importação para @RequestMapping no nível da classe
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.depoimento.dto.DepoimentoRequestDTO;
import br.com.legalconnect.depoimento.dto.DepoimentoResponseDTO;
import br.com.legalconnect.depoimento.service.DepoimentoAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Para segurança JWT
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para o módulo de Depoimentos.
 * Gerencia endpoints públicos e de administração (com segurança).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/publico/marketplace/depoimentos") // Mover o prefixo da URL para o nível da classe
@Tag(name = "Depoimentos", description = "Gerenciamento de depoimentos para o marketplace jurídico")
public class DepoimentoController {

        private final DepoimentoAppService appService;

        @Operation(summary = "Lista depoimentos para a página inicial", description = "Retorna uma lista de depoimentos aprovados, com opções de limite e ordenação aleatória.", parameters = {
                        @Parameter(name = "limit", description = "Número máximo de depoimentos a serem retornados (padrão: 5)", example = "5"),
                        @Parameter(name = "random", description = "Indica se os depoimentos devem ser aleatórios (padrão: false)", example = "true")
        }, responses = {
                        @ApiResponse(responseCode = "200", description = "Depoimentos listados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class)))
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<DepoimentoResponseDTO>>> listarParaHome(
                        @RequestParam(defaultValue = "5") int limit,
                        @RequestParam(defaultValue = "false") boolean random) {
                List<DepoimentoResponseDTO> depoimentos = appService.listarParaHome(limit, random);
                return ResponseEntity.ok(BaseResponse.<List<DepoimentoResponseDTO>>builder()
                                .data(depoimentos)
                                .message("Depoimentos listados com sucesso.")
                                .build());
        }

        @Operation(summary = "Cria um novo depoimento", description = "Cria um novo depoimento no sistema. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do depoimento a ser criado", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoRequestDTO.class))), responses = {
                        @ApiResponse(responseCode = "201", description = "Depoimento criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido")
        }, security = @SecurityRequirement(name = "bearerAuth")) // Referência ao esquema de segurança JWT
        @PostMapping("/publico/depoimentos")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> criarDepoimento(
                        @RequestBody @Valid DepoimentoRequestDTO request,
                        @RequestHeader("X-Correlation-ID") String userId) {
                request.setUserId(UUID.fromString(userId));
                DepoimentoResponseDTO novoDepoimento = appService.criarDepoimento(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(novoDepoimento)
                                .message("Depoimento criado com sucesso.")
                                .build());
        }

        @Operation(summary = "Atualiza um depoimento existente", description = "Atualiza os dados de um depoimento pelo seu ID. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", parameters = @Parameter(name = "id", description = "ID do depoimento a ser atualizado", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados atualizados do depoimento", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoRequestDTO.class))), responses = {
                        @ApiResponse(responseCode = "200", description = "Depoimento atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Depoimento não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/publico/depoimentos/{id}")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> atualizarDepoimento(
                        @PathVariable UUID id,
                        @RequestBody @Valid DepoimentoRequestDTO request) {
                DepoimentoResponseDTO depoimentoAtualizado = appService.atualizarDepoimento(id, request);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimentoAtualizado)
                                .message("Depoimento atualizado com sucesso.")
                                .build());
        }

        @Operation(summary = "Exclui um depoimento", description = "Exclui um depoimento permanentemente pelo seu ID. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", parameters = @Parameter(name = "id", description = "ID do depoimento a ser excluído", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"), responses = {
                        @ApiResponse(responseCode = "204", description = "Depoimento excluído com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Depoimento não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @DeleteMapping("/publico/depoimentos/{id}")
        public ResponseEntity<BaseResponse<Void>> excluirDepoimento(@PathVariable UUID id) {
                appService.excluirDepoimento(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseResponse.<Void>builder()
                                .message("Depoimento excluído com sucesso.")
                                .build());
        }

        @Operation(summary = "Aprova um depoimento", description = "Altera o status de um depoimento para 'APROVADO'. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", parameters = @Parameter(name = "id", description = "ID do depoimento a ser aprovado", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"), responses = {
                        @ApiResponse(responseCode = "200", description = "Depoimento aprovado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Depoimento não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/publico/depoimentos/{id}/aprovar")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> aprovarDepoimento(@PathVariable UUID id) {
                DepoimentoResponseDTO depoimentoAprovado = appService.aprovarDepoimento(id);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimentoAprovado)
                                .message("Depoimento aprovado com sucesso.")
                                .build());
        }

        @Operation(summary = "Reprova um depoimento", description = "Altera o status de um depoimento para 'REPROVADO'. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", parameters = @Parameter(name = "id", description = "ID do depoimento a ser reprovado", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"), responses = {
                        @ApiResponse(responseCode = "200", description = "Depoimento reprovado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Depoimento não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/publico/depoimentos/{id}/reprovar")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> reprovarDepoimento(@PathVariable UUID id) {
                DepoimentoResponseDTO depoimentoReprovado = appService.reprovarDepoimento(id);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimentoReprovado)
                                .message("Depoimento reprovado com sucesso.")
                                .build());
        }

        @Operation(summary = "Lista todos os depoimentos (administração)", description = "Retorna uma lista completa de todos os depoimentos, independentemente do status. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", responses = {
                        @ApiResponse(responseCode = "200", description = "Todos os depoimentos listados para administração", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @GetMapping("/publico/depoimentos/todos")
        public ResponseEntity<BaseResponse<List<DepoimentoResponseDTO>>> listarTodosAdmin() {
                List<DepoimentoResponseDTO> depoimentos = appService.listarTodos();
                return ResponseEntity.ok(BaseResponse.<List<DepoimentoResponseDTO>>builder()
                                .data(depoimentos)
                                .message("Todos os depoimentos listados para administração.")
                                .build());
        }

        @Operation(summary = "Busca um depoimento por ID (administração)", description = "Retorna um depoimento específico pelo seu ID. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", parameters = @Parameter(name = "id", description = "ID do depoimento a ser buscado", required = true, example = "a1b2c3d4-e5f6-7890-1234-567890abcdef"), responses = {
                        @ApiResponse(responseCode = "200", description = "Depoimento encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepoimentoResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Depoimento não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @GetMapping("/publico/depoimentos/{id}")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> buscarDepoimentoPorIdAdmin(@PathVariable UUID id) {
                DepoimentoResponseDTO depoimento = appService.buscarPorId(id);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimento)
                                .message("Depoimento encontrado.")
                                .build());
        }
}

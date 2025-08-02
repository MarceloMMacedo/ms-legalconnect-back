//
// Controlador REST para gerenciar os patrocinadores do marketplace jurídico.
// Este controlador lida com os endpoints de administração.
//
package br.com.legalconnect.patrocinio.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.patrocinio.dto.DestaquesRequestDTO;
import br.com.legalconnect.patrocinio.dto.DestaquesResponseDTO;
import br.com.legalconnect.patrocinio.service.PatrocinioAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para os endpoints de gerenciamento de patrocinadores
 * (admin).
 * Separado do controlador público para melhor organização e controle de acesso.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Patrocínios (Admin)", description = "Endpoints para gerenciamento de patrocinadores no marketplace jurídico (acesso administrativo)")
@RequestMapping("/api/v1/marketplace/destaques")
@PreAuthorize("hasRole('ROLE_PLATAFORMA_ADMIN')")
public class AdminDestaquesController {

        private final PatrocinioAppService patrocinioAppService;

        /**
         * Endpoint privado para listar todos os patrocinadores, independentemente do
         * status.
         * Requer a role ROLE_PLATAFORMA_ADMIN.
         *
         * @return ResponseEntity contendo a lista completa de patrocinadores e uma
         *         resposta padrão.
         */
        @Operation(summary = "Lista todos os patrocinadores (administração)", description = "Retorna uma lista completa de todos os patrocinadores, independentemente do status. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", responses = {
                        @ApiResponse(responseCode = "200", description = "Todos os patrocinadores listados", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestaquesResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @GetMapping
        public ResponseEntity<BaseResponse<List<DestaquesResponseDTO>>> getAllPatrocinios() {
                List<DestaquesResponseDTO> patrocinadores = patrocinioAppService.findAllPatrocinios();
                return ResponseEntity.ok(BaseResponse.<List<DestaquesResponseDTO>>builder()
                                .data(patrocinadores)
                                .message("Todos os patrocinadores listados.")
                                .build());
        }

        /**
         * Endpoint privado para criar um novo patrocinador.
         * A API aceita um DTO polimórfico, permitindo a criação de diferentes tipos de
         * patrocinadores.
         *
         * @param requestDTO DTO com os dados do novo patrocinador.
         * @return ResponseEntity com o patrocinador criado e uma resposta padrão.
         */
        @Operation(summary = "Cria um novo patrocinador", description = "Cria um novo patrocinador. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", responses = {
                        @ApiResponse(responseCode = "201", description = "Patrocinador criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestaquesResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @PostMapping
        public ResponseEntity<BaseResponse<DestaquesResponseDTO>> createPatrocinio(
                        @RequestBody @Valid DestaquesRequestDTO requestDTO) {
                DestaquesResponseDTO novoPatrocinio = patrocinioAppService.createPatrocinio(requestDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<DestaquesResponseDTO>builder()
                                .data(novoPatrocinio)
                                .message("Patrocinador criado com sucesso.")
                                .build());
        }

        /**
         * Endpoint privado para atualizar um patrocinador existente.
         * A API aceita um DTO polimórfico para a atualização.
         *
         * @param id         ID do patrocinador a ser atualizado.
         * @param requestDTO DTO com os dados atualizados.
         * @return ResponseEntity com o patrocinador atualizado e uma resposta padrão.
         */
        @Operation(summary = "Atualiza um patrocinador existente", description = "Atualiza um patrocinador pelo seu ID. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", responses = {
                        @ApiResponse(responseCode = "200", description = "Patrocinador atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestaquesResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Patrocinador não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/{id}")
        public ResponseEntity<BaseResponse<DestaquesResponseDTO>> updatePatrocinio(
                        @PathVariable UUID id,
                        @RequestBody @Valid DestaquesRequestDTO requestDTO) {
                DestaquesResponseDTO updatedPatrocinio = patrocinioAppService.updatePatrocinio(id, requestDTO);
                return ResponseEntity.ok(BaseResponse.<DestaquesResponseDTO>builder()
                                .data(updatedPatrocinio)
                                .message("Patrocinador atualizado com sucesso.")
                                .build());
        }

        /**
         * Endpoint privado para mudar o status de um patrocinador.
         *
         * @param id     ID do patrocinador.
         * @param status O novo status (e.g., "ACTIVE", "INACTIVE").
         * @return ResponseEntity com o patrocinador atualizado e uma resposta padrão.
         */
        @Operation(summary = "Muda o status de um patrocinador", description = "Altera o status de um patrocinador (ex: de INACTIVE para ACTIVE). Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", responses = {
                        @ApiResponse(responseCode = "200", description = "Status do patrocinador alterado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestaquesResponseDTO.class))),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Patrocinador não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @PatchMapping("/{id}/status")
        public ResponseEntity<BaseResponse<DestaquesResponseDTO>> updatePatrocinioStatus(
                        @PathVariable UUID id,
                        @RequestParam String status) {
                DestaquesResponseDTO updatedPatrocinio = patrocinioAppService.updatePatrocinioStatus(id, status);
                return ResponseEntity.ok(BaseResponse.<DestaquesResponseDTO>builder()
                                .data(updatedPatrocinio)
                                .message("Status do patrocinador alterado com sucesso.")
                                .build());
        }

        /**
         * Endpoint privado para excluir um patrocinador.
         *
         * @param id ID do patrocinador a ser excluído.
         * @return ResponseEntity com status 204 (No Content) em caso de sucesso.
         */
        @Operation(summary = "Exclui um patrocinador", description = "Exclui um patrocinador permanentemente pelo seu ID. Requer autenticação de administrador (ROLE_PLATAFORMA_ADMIN).", responses = {
                        @ApiResponse(responseCode = "204", description = "Patrocinador excluído com sucesso"),
                        @ApiResponse(responseCode = "403", description = "Acesso proibido"),
                        @ApiResponse(responseCode = "404", description = "Patrocinador não encontrado")
        }, security = @SecurityRequirement(name = "bearerAuth"))
        @DeleteMapping("/{id}")
        public ResponseEntity<BaseResponse<Void>> deletePatrocinio(@PathVariable UUID id) {
                patrocinioAppService.deletePatrocinio(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseResponse.<Void>builder()
                                .message("Patrocinador excluído com sucesso.")
                                .build());
        }
}
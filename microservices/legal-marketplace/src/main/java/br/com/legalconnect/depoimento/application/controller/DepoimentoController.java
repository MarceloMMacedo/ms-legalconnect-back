package br.com.legalconnect.depoimento.application.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.depoimento.application.dto.DepoimentoRequestDTO;
import br.com.legalconnect.depoimento.application.dto.DepoimentoResponseDTO;
import br.com.legalconnect.depoimento.application.service.DepoimentoAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para o módulo de Depoimentos.
 * Gerencia endpoints públicos e de administração (com segurança).
 */
@RestController
@RequiredArgsConstructor
public class DepoimentoController {

        private final DepoimentoAppService appService;

        // --- Endpoints Públicos ---

        /**
         * Lista depoimentos para exibição na página inicial, com opções de limite e
         * ordenação.
         * Exemplo: GET /api/v1/public/depoimentos?limit=5&random=true
         * 
         * @param limit  O número máximo de depoimentos a serem retornados (padrão: 5).
         * @param random Booleano para indicar se os depoimentos devem ser aleatórios
         *               (padrão: false).
         * @return ResponseEntity com a lista de depoimentos.
         */
        @GetMapping("/api/v1/publico/depoimentos")
        public ResponseEntity<BaseResponse<List<DepoimentoResponseDTO>>> listarParaHome(
                        @RequestParam(defaultValue = "5") int limit,
                        @RequestParam(defaultValue = "false") boolean random) {
                List<DepoimentoResponseDTO> depoimentos = appService.listarParaHome(limit, random);
                return ResponseEntity.ok(BaseResponse.<List<DepoimentoResponseDTO>>builder()
                                .data(depoimentos)
                                .message("Depoimentos listados com sucesso.")
                                .build());
        }

        // --- Endpoints de Administração (requer ROLE_PLATAFORMA_ADMIN) ---

        /**
         * Cria um novo depoimento.
         * Exemplo: POST /api/v1/publico/depoimentos
         * 
         * @param request O DTO com os dados do depoimento a ser criado.
         * @return ResponseEntity com o depoimento criado e status 201 CREATED.
         */
        @PostMapping("/api/v1/publico/depoimentos")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> criarDepoimento(
                        @RequestBody @Valid DepoimentoRequestDTO request) {
                DepoimentoResponseDTO novoDepoimento = appService.criarDepoimento(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(novoDepoimento)
                                .message("Depoimento criado com sucesso.")
                                .build());
        }

        /**
         * Atualiza um depoimento existente.
         * Exemplo: PUT /api/v1/publico/depoimentos/{id}
         * 
         * @param id      O ID do depoimento a ser atualizado.
         * @param request O DTO com os dados atualizados do depoimento.
         * @return ResponseEntity com o depoimento atualizado.
         */
        @PutMapping("/api/v1/publico/depoimentos/{id}")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> atualizarDepoimento(
                        @PathVariable UUID id,
                        @RequestBody @Valid DepoimentoRequestDTO request) {
                DepoimentoResponseDTO depoimentoAtualizado = appService.atualizarDepoimento(id, request);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimentoAtualizado)
                                .message("Depoimento atualizado com sucesso.")
                                .build());
        }

        /**
         * Exclui um depoimento pelo ID.
         * Exemplo: DELETE /api/v1/publico/depoimentos/{id}
         * 
         * @param id O ID do depoimento a ser excluído.
         * @return ResponseEntity com status 204 NO CONTENT.
         */
        @DeleteMapping("/api/v1/publico/depoimentos/{id}")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<Void>> excluirDepoimento(@PathVariable UUID id) {
                appService.excluirDepoimento(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseResponse.<Void>builder()
                                .message("Depoimento excluído com sucesso.")
                                .build());
        }

        /**
         * Aprova um depoimento.
         * Exemplo: PATCH /api/v1/publico/depoimentos/{id}/aprovar
         * 
         * @param id O ID do depoimento a ser aprovado.
         * @return ResponseEntity com o depoimento aprovado.
         */
        @PatchMapping("/api/v1/publico/depoimentos/{id}/aprovar")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> aprovarDepoimento(@PathVariable UUID id) {
                DepoimentoResponseDTO depoimentoAprovado = appService.aprovarDepoimento(id);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimentoAprovado)
                                .message("Depoimento aprovado com sucesso.")
                                .build());
        }

        /**
         * Reprova um depoimento.
         * Exemplo: PATCH /api/v1/publico/depoimentos/{id}/reprovar
         * 
         * @param id O ID do depoimento a ser reprovado.
         * @return ResponseEntity com o depoimento reprovado.
         */
        @PatchMapping("/api/v1/publico/depoimentos/{id}/reprovar")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> reprovarDepoimento(@PathVariable UUID id) {
                DepoimentoResponseDTO depoimentoReprovado = appService.reprovarDepoimento(id);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimentoReprovado)
                                .message("Depoimento reprovado com sucesso.")
                                .build());
        }

        /**
         * Lista todos os depoimentos (uso administrativo).
         * Exemplo: GET /api/v1/publico/depoimentos
         * 
         * @return ResponseEntity com a lista de todos os depoimentos.
         */
        @GetMapping("/api/v1/publico/depoimentos")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<List<DepoimentoResponseDTO>>> listarTodosAdmin() {
                List<DepoimentoResponseDTO> depoimentos = appService.listarTodos();
                return ResponseEntity.ok(BaseResponse.<List<DepoimentoResponseDTO>>builder()
                                .data(depoimentos)
                                .message("Todos os depoimentos listados para administração.")
                                .build());
        }

        /**
         * Busca um depoimento específico pelo ID (uso administrativo).
         * Exemplo: GET /api/v1/publico/depoimentos/{id}
         * 
         * @param id O ID do depoimento.
         * @return ResponseEntity com o depoimento encontrado.
         */
        @GetMapping("/api/v1/publico/depoimentos/{id}")
        // @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
        public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> buscarDepoimentoPorIdAdmin(@PathVariable UUID id) {
                DepoimentoResponseDTO depoimento = appService.buscarPorId(id);
                return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                                .data(depoimento)
                                .message("Depoimento encontrado.")
                                .build());
        }
}
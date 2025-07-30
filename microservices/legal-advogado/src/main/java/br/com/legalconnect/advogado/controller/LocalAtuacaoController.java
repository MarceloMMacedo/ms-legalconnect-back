package br.com.legalconnect.advogado.controller;

import static br.com.legalconnect.enums.StatusResponse.SUCESSO;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.advogado.dto.response.LocalAtuacaoResponseDTO;
import br.com.legalconnect.advogado.service.LocalAtuacaoService;
import br.com.legalconnect.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para o módulo de Locais de Atuação (dados mestre).
 * Oferece endpoints para consultar os locais de atuação disponíveis.
 */
@RestController
@RequestMapping("/api/v1/advogados/locais-atuacao")
@Tag(name = "Locais de Atuação", description = "Gerenciamento de dados mestre de Locais de Atuação")
public class LocalAtuacaoController {

        private final LocalAtuacaoService localAtuacaoService;

        @Autowired
        public LocalAtuacaoController(LocalAtuacaoService localAtuacaoService) {
                this.localAtuacaoService = localAtuacaoService;
        }

        /**
         * Busca um Local de Atuação pelo ID.
         * Funcionalidade Completa: Consulta de um dado mestre específico.
         * Regras de Negócio: N/A (apenas busca por ID).
         *
         * @param id ID do Local de Atuação.
         * @return ResponseEntity com o DTO do Local de Atuação.
         */
        @Operation(summary = "Busca um local de atuação pelo ID", description = "Retorna os detalhes de um local de atuação específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Local de Atuação encontrado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Local de Atuação não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<LocalAtuacaoResponseDTO>> getLocalAtuacaoById(
                        @Parameter(description = "ID do Local de Atuação") @PathVariable UUID id) {
                LocalAtuacaoResponseDTO response = localAtuacaoService.findLocalAtuacaoById(id);
                return ResponseEntity.ok(BaseResponse.<LocalAtuacaoResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Local de Atuação encontrado com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todos os Locais de Atuação.
         * Funcionalidade Completa: Listagem de todos os locais disponíveis para seleção
         * pelos advogados.
         * Regras de Negócio: N/A (apenas listagem).
         *
         * @return ResponseEntity com a lista de DTOs de Locais de Atuação.
         */
        @Operation(summary = "Lista todos os locais de atuação", description = "Retorna uma lista de todos os locais de atuação cadastrados.", responses = {
                        @ApiResponse(responseCode = "200", description = "Locais de Atuação listados com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<LocalAtuacaoResponseDTO>>> getAllLocaisAtuacao() {
                List<LocalAtuacaoResponseDTO> response = localAtuacaoService.findAllLocaisAtuacao();
                return ResponseEntity.ok(BaseResponse.<List<LocalAtuacaoResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Locais de Atuação listados com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }
}
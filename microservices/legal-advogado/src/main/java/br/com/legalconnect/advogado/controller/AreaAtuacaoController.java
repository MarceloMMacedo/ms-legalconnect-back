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

import br.com.legalconnect.advogado.application.dto.response.AreaAtuacaoResponseDTO;
import br.com.legalconnect.advogado.application.service.AreaAtuacaoService;
import br.com.legalconnect.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para o módulo de Áreas de Atuação (dados mestre).
 * Oferece endpoints para consultar as áreas de atuação disponíveis.
 */
@RestController
@RequestMapping("/api/v1/advogados/areas-atuacao")
@Tag(name = "Áreas de Atuação", description = "Gerenciamento de dados mestre de Áreas de Atuação")
public class AreaAtuacaoController {

        private final AreaAtuacaoService areaAtuacaoService;

        @Autowired
        public AreaAtuacaoController(AreaAtuacaoService areaAtuacaoService) {
                this.areaAtuacaoService = areaAtuacaoService;
        }

        /**
         * Busca uma Área de Atuação pelo ID.
         * Funcionalidade Completa: Consulta de um dado mestre específico.
         * Regras de Negócio: N/A (apenas busca por ID).
         *
         * @param id ID da Área de Atuação.
         * @return ResponseEntity com o DTO da Área de Atuação.
         */
        @Operation(summary = "Busca uma área de atuação pelo ID", description = "Retorna os detalhes de uma área de atuação específica.", responses = {
                        @ApiResponse(responseCode = "200", description = "Área de Atuação encontrada com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Área de Atuação não encontrada"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<AreaAtuacaoResponseDTO>> getAreaAtuacaoById(
                        @Parameter(description = "ID da Área de Atuação") @PathVariable UUID id) {
                AreaAtuacaoResponseDTO response = areaAtuacaoService.findAreaAtuacaoById(id);
                return ResponseEntity.ok(BaseResponse.<AreaAtuacaoResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Área de Atuação encontrada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todas as Áreas de Atuação.
         * Funcionalidade Completa: Listagem de todas as áreas disponíveis para seleção
         * pelos advogados.
         * Regras de Negócio: N/A (apenas listagem).
         *
         * @return ResponseEntity com a lista de DTOs de Áreas de Atuação.
         */
        @Operation(summary = "Lista todas as áreas de atuação", description = "Retorna uma lista de todas as áreas de atuação cadastradas.", responses = {
                        @ApiResponse(responseCode = "200", description = "Áreas de Atuação listadas com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<AreaAtuacaoResponseDTO>>> getAllAreasAtuacao() {
                List<AreaAtuacaoResponseDTO> response = areaAtuacaoService.findAllAreasAtuacao();
                return ResponseEntity.ok(BaseResponse.<List<AreaAtuacaoResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Áreas de Atuação listadas com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }
}
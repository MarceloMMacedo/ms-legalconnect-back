package br.com.legalconnect.advogado.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.advogado.dto.response.AreaAtuacaoResponseDTO;
import br.com.legalconnect.advogado.service.AreaAtuacaoService;
import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.enums.StatusResponse;
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
                                .status(StatusResponse.SUCESSO)
                                .message("Área de Atuação encontrada com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

}
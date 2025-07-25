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

import br.com.legalconnect.advogado.application.dto.response.TipoAtendimentoResponseDTO;
import br.com.legalconnect.advogado.application.service.TipoAtendimentoService;
import br.com.legalconnect.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para o módulo de Tipos de Atendimento (dados mestre).
 * Oferece endpoints para consultar os tipos de atendimento disponíveis.
 */
@RestController
@RequestMapping("/api/v1/advogados/tipos-atendimento")
@Tag(name = "Tipos de Atendimento", description = "Gerenciamento de dados mestre de Tipos de Atendimento")
public class TipoAtendimentoController {

        private final TipoAtendimentoService tipoAtendimentoService;

        @Autowired
        public TipoAtendimentoController(TipoAtendimentoService tipoAtendimentoService) {
                this.tipoAtendimentoService = tipoAtendimentoService;
        }

        /**
         * Busca um Tipo de Atendimento pelo ID.
         * Funcionalidade Completa: Consulta de um dado mestre específico.
         * Regras de Negócio: N/A (apenas busca por ID).
         *
         * @param id ID do Tipo de Atendimento.
         * @return ResponseEntity com o DTO do Tipo de Atendimento.
         */
        @Operation(summary = "Busca um tipo de atendimento pelo ID", description = "Retorna os detalhes de um tipo de atendimento específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Tipo de Atendimento encontrado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Tipo de Atendimento não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<TipoAtendimentoResponseDTO>> getTipoAtendimentoById(
                        @Parameter(description = "ID do Tipo de Atendimento") @PathVariable UUID id) {
                TipoAtendimentoResponseDTO response = tipoAtendimentoService.findTipoAtendimentoById(id);
                return ResponseEntity.ok(BaseResponse.<TipoAtendimentoResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Tipo de Atendimento encontrado com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todos os Tipos de Atendimento.
         * Funcionalidade Completa: Listagem de todos os tipos disponíveis para seleção
         * pelos advogados.
         * Regras de Negócio: N/A (apenas listagem).
         *
         * @return ResponseEntity com a lista de DTOs de Tipos de Atendimento.
         */
        @Operation(summary = "Lista todos os tipos de atendimento", description = "Retorna uma lista de todos os tipos de atendimento cadastrados.", responses = {
                        @ApiResponse(responseCode = "200", description = "Tipos de Atendimento listados com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<TipoAtendimentoResponseDTO>>> getAllTiposAtendimento() {
                List<TipoAtendimentoResponseDTO> response = tipoAtendimentoService.findAllTiposAtendimento();
                return ResponseEntity.ok(BaseResponse.<List<TipoAtendimentoResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Tipos de Atendimento listados com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }
}
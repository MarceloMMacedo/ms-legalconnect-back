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

import br.com.legalconnect.advogado.application.dto.response.IdiomaResponseDTO;
import br.com.legalconnect.advogado.application.service.IdiomaService;
import br.com.legalconnect.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para o módulo de Idiomas (dados mestre).
 * Oferece endpoints para consultar os idiomas disponíveis.
 */
@RestController
@RequestMapping("/api/v1/advogados/idiomas")
@Tag(name = "Idiomas", description = "Gerenciamento de dados mestre de Idiomas")
public class IdiomaController {

        private final IdiomaService idiomaService;

        @Autowired
        public IdiomaController(IdiomaService idiomaService) {
                this.idiomaService = idiomaService;
        }

        /**
         * Busca um Idioma pelo ID.
         * Funcionalidade Completa: Consulta de um dado mestre específico.
         * Regras de Negócio: N/A (apenas busca por ID).
         *
         * @param id ID do Idioma.
         * @return ResponseEntity com o DTO do Idioma.
         */
        @Operation(summary = "Busca um idioma pelo ID", description = "Retorna os detalhes de um idioma específico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Idioma encontrado com sucesso"),
                        @ApiResponse(responseCode = "404", description = "Idioma não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<IdiomaResponseDTO>> getIdiomaById(
                        @Parameter(description = "ID do idioma") @PathVariable UUID id) {
                IdiomaResponseDTO response = idiomaService.findIdiomaById(id);
                return ResponseEntity.ok(BaseResponse.<IdiomaResponseDTO>builder()
                                .status(SUCESSO)
                                .message("Idioma encontrado com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }

        /**
         * Lista todos os Idiomas.
         * Funcionalidade Completa: Listagem de todos os idiomas disponíveis para
         * seleção pelos advogados.
         * Regras de Negócio: N/A (apenas listagem).
         *
         * @return ResponseEntity com a lista de DTOs de Idiomas.
         */
        @Operation(summary = "Lista todos os idiomas", description = "Retorna uma lista de todos os idiomas cadastrados.", responses = {
                        @ApiResponse(responseCode = "200", description = "Idiomas listados com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<IdiomaResponseDTO>>> getAllIdiomas() {
                List<IdiomaResponseDTO> response = idiomaService.findAllIdiomas();
                return ResponseEntity.ok(BaseResponse.<List<IdiomaResponseDTO>>builder()
                                .status(SUCESSO)
                                .message("Idiomas listados com sucesso.")
                                .data(response)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }
}
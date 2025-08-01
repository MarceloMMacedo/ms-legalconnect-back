//
// Controlador REST para gerenciar os patrocinadores do marketplace jurídico.
// Este controlador é o ponto de entrada da API, lidando com as requisições HTTP
// e orquestrando o serviço de aplicação para executar a lógica de negócio.
//
package br.com.legalconnect.patrocinio.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.patrocinio.dto.DestaquesResponseDTO;
import br.com.legalconnect.patrocinio.service.PatrocinioAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Patrocínios", description = "Endpoints para gerenciamento de patrocinadores no marketplace jurídico")
@RequestMapping("/api/v1/publico/destaques")
public class DestaquesController {

        private final PatrocinioAppService patrocinioAppService;

        // =================================================================================================================
        // Endpoint Público
        // =================================================================================================================

        /**
         * Endpoint público para listar todos os patrocinadores com status ATIVO.
         * Retorna uma lista de DTOs polimórficos que podem representar diferentes tipos
         * de patrocínios (Eventos, Escritórios, etc.).
         *
         * @return ResponseEntity contendo a lista de patrocinadores ativos e uma
         *         resposta padrão.
         */
        @Operation(summary = "Lista patrocinadores ativos", description = "Retorna uma lista de patrocinadores com status ATIVO. A resposta pode conter diferentes tipos de patrocinadores.", responses = {
                        @ApiResponse(responseCode = "200", description = "Patrocinadores ativos listados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DestaquesResponseDTO.class)))
        })
        @GetMapping
        public ResponseEntity<BaseResponse<List<DestaquesResponseDTO>>> getActivePatrocinios() {
                List<DestaquesResponseDTO> patrocinadores = patrocinioAppService.findActivePatrocinios();
                return ResponseEntity.ok(BaseResponse.<List<DestaquesResponseDTO>>builder()
                                .data(patrocinadores)
                                .message("Patrocinadores ativos listados com sucesso.")
                                .build());
        }
}
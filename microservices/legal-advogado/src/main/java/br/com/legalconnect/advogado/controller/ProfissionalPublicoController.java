package br.com.legalconnect.advogado.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.advogado.service.ProfissionalService;
import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.enums.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para dados mestre de Localizações.
 * Oferece endpoints para consultar estados e cidades disponíveis.
 */
@RestController
@RequestMapping("/api/v1/publico/advogados/profissionais/localizacoes") // Caminho da requisição ajustado
@Tag(name = "Localizações", description = "Gerenciamento de dados mestre de Localizações (Estados e Cidades)")
public class ProfissionalPublicoController {

    private final ProfissionalService pessoaService;

    @Autowired
    public ProfissionalPublicoController(ProfissionalService pessoaService) {
        this.pessoaService = pessoaService;
    }

    /**
     * Lista todas as localizações (estados e cidades) onde há profissionais.
     * Retorna um mapa onde a chave é o estado (UF) e o valor é uma lista de
     * cidades.
     *
     * @return ResponseEntity com o mapa de localizações.
     */
    @Operation(summary = "Lista todas as localizações (estados e cidades)", description = "Retorna um mapa de estados e suas respectivas cidades onde há advogados cadastrados.", responses = {
            @ApiResponse(responseCode = "200", description = "Localizações listadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<BaseResponse<Map<String, List<String>>>> getAllLocalizacoes() {
        Map<String, List<String>> response = pessoaService.listarLocalizacoesDisponiveis();
        return ResponseEntity.ok(BaseResponse.<Map<String, List<String>>>builder()
                .status(StatusResponse.SUCESSO)
                .message("Localizações listadas com sucesso.")
                .data(response)
                .timestamp(java.time.LocalDateTime.now())
                .build());
    }
}

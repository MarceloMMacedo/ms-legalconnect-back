package br.com.legalconnect.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.dto.EmpresaRequestDTO;
import br.com.legalconnect.dto.EmpresaResponseDTO;
import br.com.legalconnect.enums.StatusResponse;
import br.com.legalconnect.service.ServicoEmpresa;
import jakarta.validation.Valid;

/**
 * @class ControladorEmpresa
 * @brief Controlador REST para gerenciar operações relacionadas a Empresas.
 *        Expõe endpoints para CRUD de Empresas.
 */
@RestController
@RequestMapping("/api/v1/empresas")
public class ControladorEmpresa {

    private final ServicoEmpresa servicoEmpresa;

    @Autowired
    public ControladorEmpresa(ServicoEmpresa servicoEmpresa) {
        this.servicoEmpresa = servicoEmpresa;
    }

    /**
     * @brief Cadastra uma nova Empresa.
     * @param requestDTO DTO com os dados da Empresa a ser cadastrada.
     * @return ResponseEntity com o DTO da Empresa cadastrada e status 201 Created.
     */
    @PostMapping
    public ResponseEntity<BaseResponse<EmpresaResponseDTO>> cadastrarEmpresa(
            @Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO responseDTO = servicoEmpresa.cadastrarEmpresa(requestDTO); // Chamando o método original do
                                                                                      // serviço
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.<EmpresaResponseDTO>builder()
                        .message("Empresa cadastrada com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .data(responseDTO)
                        .build());
    }

    /**
     * @brief Busca uma Empresa por ID.
     * @param id ID da Empresa a ser buscada.
     * @return ResponseEntity com o DTO da Empresa encontrada e status 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EmpresaResponseDTO>> buscarEmpresaPorId(@PathVariable UUID id) {
        EmpresaResponseDTO responseDTO = servicoEmpresa.buscarEmpresaPorId(id); // Chamando o método original do serviço
        return ResponseEntity.ok(
                BaseResponse.<EmpresaResponseDTO>builder()
                        .message("Empresa encontrada.")
                        .status(StatusResponse.SUCESSO)
                        .data(responseDTO)
                        .build());
    }

    /**
     * @brief Lista todas as Empresas com paginação.
     * @param pageable Objeto Pageable para configuração da paginação (ex:
     *                 ?page=0&size=10&sort=nomeFantasia,asc).
     * @return ResponseEntity com uma página de DTOs de Empresas e status 200 OK.
     */
    @GetMapping
    public ResponseEntity<BaseResponse<Page<EmpresaResponseDTO>>> listarEmpresas(
            @PageableDefault(size = 10, page = 0, sort = "nomeFantasia") Pageable pageable) {
        Page<EmpresaResponseDTO> responsePage = servicoEmpresa.listarEmpresas(pageable); // Chamando o método original
                                                                                         // do serviço
        return ResponseEntity.ok(
                BaseResponse.<Page<EmpresaResponseDTO>>builder()
                        .message("Lista de empresas recuperada com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .data(responsePage)
                        .build());
    }

    /**
     * @brief Atualiza os dados de uma Empresa existente.
     * @param id         ID da Empresa a ser atualizada.
     * @param requestDTO DTO com os dados atualizados da Empresa.
     * @return ResponseEntity com o DTO da Empresa atualizada e status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<EmpresaResponseDTO>> atualizarEmpresa(
            @PathVariable UUID id,
            @Valid @RequestBody EmpresaRequestDTO requestDTO) {
        EmpresaResponseDTO responseDTO = servicoEmpresa.atualizarEmpresa(id, requestDTO); // Chamando o método original
                                                                                          // do serviço
        return ResponseEntity.ok(
                BaseResponse.<EmpresaResponseDTO>builder()
                        .message("Empresa atualizada com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .data(responseDTO)
                        .build());
    }

    /**
     * @brief Exclui uma Empresa pelo ID.
     * @param id ID da Empresa a ser excluída.
     * @return ResponseEntity com status 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> excluirEmpresa(@PathVariable UUID id) {
        servicoEmpresa.excluirEmpresa(id); // Chamando o método original do serviço
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                BaseResponse.<Void>builder()
                        .message("Empresa excluída com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .build());
    }
}

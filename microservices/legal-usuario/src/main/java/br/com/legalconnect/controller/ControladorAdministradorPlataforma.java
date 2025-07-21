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
import br.com.legalconnect.dto.AdministradorRequestDTO;
import br.com.legalconnect.dto.AdministradorResponseDTO;
import br.com.legalconnect.enums.StatusResponse;
import br.com.legalconnect.service.ServicoAdministrador;
import jakarta.validation.Valid;

/**
 * @class ControladorAdministradorPlataforma
 * @brief Controlador REST para gerenciar operações relacionadas a
 *        Administradores da Plataforma.
 *        Expõe endpoints para CRUD de Administradores.
 */
@RestController
@RequestMapping("/api/v1/administradores-plataforma")
public class ControladorAdministradorPlataforma {

    private final ServicoAdministrador servicoAdministrador;

    @Autowired
    public ControladorAdministradorPlataforma(ServicoAdministrador servicoAdministrador) {
        this.servicoAdministrador = servicoAdministrador;
    }

    /**
     * @brief Cadastra um novo Administrador da Plataforma.
     * @param requestDTO DTO com os dados do Administrador a ser cadastrado.
     * @return ResponseEntity com o DTO do Administrador cadastrado e status 201
     *         Created.
     */
    @PostMapping
    public ResponseEntity<BaseResponse<AdministradorResponseDTO>> cadastrarAdministrador(
            @Valid @RequestBody AdministradorRequestDTO requestDTO) {
        AdministradorResponseDTO responseDTO = servicoAdministrador.cadastrarAdministrador(requestDTO); // Usar o método
                                                                                                        // 'cadastrar'
        // da classe base
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.<AdministradorResponseDTO>builder()
                        .message("Administrador cadastrado com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .data(responseDTO)
                        .build());
    }

    /**
     * @brief Busca um Administrador da Plataforma por ID.
     * @param id ID do Administrador a ser buscado.
     * @return ResponseEntity com o DTO do Administrador encontrado e status 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AdministradorResponseDTO>> buscarAdministradorPorId(@PathVariable UUID id) {
        AdministradorResponseDTO responseDTO = servicoAdministrador.buscarAdministradorPorId(id); // Usar o método
                                                                                                  // 'buscarPorId' da
        // classe base
        return ResponseEntity.ok(
                BaseResponse.<AdministradorResponseDTO>builder()
                        .message("Administrador encontrado.")
                        .status(StatusResponse.SUCESSO)
                        .data(responseDTO)
                        .build());
    }

    /**
     * @brief Lista todos os Administradores da Plataforma com paginação.
     * @param pageable Objeto Pageable para configuração da paginação (ex:
     *                 ?page=0&size=10&sort=nomeCompleto,asc).
     * @return ResponseEntity com uma página de DTOs de Administradores e status 200
     *         OK.
     */
    @GetMapping
    public ResponseEntity<BaseResponse<Page<AdministradorResponseDTO>>> listarAdministradores(
            @PageableDefault(size = 10, page = 0, sort = "nomeCompleto") Pageable pageable) {
        Page<AdministradorResponseDTO> responsePage = servicoAdministrador.listarAdministradores(pageable); // Usar o
                                                                                                            // método
                                                                                                            // 'listar'
        // da classe base
        return ResponseEntity.ok(
                BaseResponse.<Page<AdministradorResponseDTO>>builder()
                        .message("Lista de administradores recuperada com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .data(responsePage)
                        .build());
    }

    /**
     * @brief Atualiza os dados de um Administrador da Plataforma existente.
     * @param id         ID do Administrador a ser atualizado.
     * @param requestDTO DTO com os dados atualizados do Administrador.
     * @return ResponseEntity com o DTO do Administrador atualizado e status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AdministradorResponseDTO>> atualizarAdministrador(
            @PathVariable UUID id,
            @Valid @RequestBody AdministradorRequestDTO requestDTO) {
        AdministradorResponseDTO responseDTO = servicoAdministrador.atualizarAdministrador(id, requestDTO); // Usar o
                                                                                                            // método
        // 'atualizar' da classe
        // base
        return ResponseEntity.ok(
                BaseResponse.<AdministradorResponseDTO>builder()
                        .message("Administrador atualizado com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .data(responseDTO)
                        .build());
    }

    /**
     * @brief Exclui um Administrador da Plataforma pelo ID.
     * @param id ID do Administrador a ser excluído.
     * @return ResponseEntity com status 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> excluirAdministrador(@PathVariable UUID id) {
        servicoAdministrador.excluirAdministrador(id); // Usar o método 'excluir' da classe base
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body( // Retorna 204 No Content com um corpo de sucesso
                BaseResponse.<Void>builder()
                        .message("Administrador excluído com sucesso.")
                        .status(StatusResponse.SUCESSO)
                        .build());
    }
}

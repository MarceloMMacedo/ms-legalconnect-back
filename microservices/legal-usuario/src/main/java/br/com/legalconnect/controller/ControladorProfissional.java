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
import br.com.legalconnect.dto.ProfissionalRequestDTO;
import br.com.legalconnect.dto.ProfissionalResponseDTO;
import br.com.legalconnect.enums.StatusResponse;
import br.com.legalconnect.service.ServicoProfissional;
import jakarta.validation.Valid;

/**
 * @class ControladorProfissional
 * @brief Controlador REST para gerenciar operações relacionadas a
 *        Profissionais.
 *        Expõe endpoints para CRUD de Profissionais.
 */
@RestController
@RequestMapping("/api/v1/usuarios/profissionais")
public class ControladorProfissional {

        private final ServicoProfissional servicoProfissional;

        @Autowired
        public ControladorProfissional(ServicoProfissional servicoProfissional) {
                this.servicoProfissional = servicoProfissional;
        }

        /**
         * @brief Cadastra um novo Profissional.
         * @param requestDTO DTO com os dados do Profissional a ser cadastrado.
         * @return ResponseEntity com o DTO do Profissional cadastrado e status 201
         *         Created.
         */
        @PostMapping
        public ResponseEntity<BaseResponse<ProfissionalResponseDTO>> cadastrarProfissional(
                        @Valid @RequestBody ProfissionalRequestDTO requestDTO) {
                ProfissionalResponseDTO responseDTO = servicoProfissional.cadastrarProfissional(requestDTO); // Chamando
                                                                                                             // o
                                                                                                             // método
                                                                                                             // original
                                                                                                             // do
                                                                                                             // serviço
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                BaseResponse.<ProfissionalResponseDTO>builder()
                                                .message("Profissional cadastrado com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responseDTO)
                                                .build());
        }

        /**
         * @brief Busca um Profissional por ID.
         * @param id ID do Profissional a ser buscado.
         * @return ResponseEntity com o DTO do Profissional encontrado e status 200 OK.
         */
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<ProfissionalResponseDTO>> buscarProfissionalPorId(@PathVariable UUID id) {
                ProfissionalResponseDTO responseDTO = servicoProfissional.buscarProfissionalPorId(id); // Chamando o
                                                                                                       // método
                                                                                                       // original do
                                                                                                       // serviço
                return ResponseEntity.ok(
                                BaseResponse.<ProfissionalResponseDTO>builder()
                                                .message("Profissional encontrado.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responseDTO)
                                                .build());
        }

        /**
         * @brief Lista todos os Profissionais com paginação.
         * @param pageable Objeto Pageable para configuração da paginação (ex:
         *                 ?page=0&size=10&sort=nomeCompleto,asc).
         * @return ResponseEntity com uma página de DTOs de Profissionais e status 200
         *         OK.
         */
        @GetMapping
        public ResponseEntity<BaseResponse<Page<ProfissionalResponseDTO>>> listarProfissionais(
                        @PageableDefault(size = 10, page = 0, sort = "nomeCompleto") Pageable pageable) {
                Page<ProfissionalResponseDTO> responsePage = servicoProfissional.listarProfissionais(pageable); // Chamando
                                                                                                                // o
                                                                                                                // método
                                                                                                                // original
                                                                                                                // do
                                                                                                                // serviço
                return ResponseEntity.ok(
                                BaseResponse.<Page<ProfissionalResponseDTO>>builder()
                                                .message("Lista de profissionais recuperada com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responsePage)
                                                .build());
        }

        /**
         * @brief Atualiza os dados de um Profissional existente.
         * @param id         ID do Profissional a ser atualizado.
         * @param requestDTO DTO com os dados atualizados do Profissional.
         * @return ResponseEntity com o DTO do Profissional atualizado e status 200 OK.
         */
        @PutMapping("/{id}")
        public ResponseEntity<BaseResponse<ProfissionalResponseDTO>> atualizarProfissional(
                        @PathVariable UUID id,
                        @Valid @RequestBody ProfissionalRequestDTO requestDTO) {
                ProfissionalResponseDTO responseDTO = servicoProfissional.atualizarProfissional(id, requestDTO); // Chamando
                                                                                                                 // o
                                                                                                                 // método
                                                                                                                 // original
                                                                                                                 // do
                                                                                                                 // serviço
                return ResponseEntity.ok(
                                BaseResponse.<ProfissionalResponseDTO>builder()
                                                .message("Profissional atualizado com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responseDTO)
                                                .build());
        }

        /**
         * @brief Exclui um Profissional pelo ID.
         * @param id ID do Profissional a ser excluído.
         * @return ResponseEntity com status 204 No Content.
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<BaseResponse<Void>> excluirProfissional(@PathVariable UUID id) {
                servicoProfissional.excluirProfissional(id); // Chamando o método original do serviço
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                                BaseResponse.<Void>builder()
                                                .message("Profissional excluído com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .build());
        }
}

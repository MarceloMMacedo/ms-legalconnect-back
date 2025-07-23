package br.com.legalconnect.controller;

import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.dto.ClienteRequestDTO;
import br.com.legalconnect.dto.ClienteResponseDTO;
import br.com.legalconnect.dto.UserRequestDTO;
import br.com.legalconnect.enums.StatusResponse;
import br.com.legalconnect.service.ServicoCliente;
import jakarta.validation.Valid;

/**
 * @class ControladorCliente
 * @brief Controlador REST para gerenciar operações relacionadas a Clientes.
 *        Expõe endpoints para CRUD de Clientes.
 */
@RestController
@RequestMapping("/api/v1/clientes")
public class ControladorCliente {

        private final ServicoCliente servicoCliente;

        @Autowired
        public ControladorCliente(ServicoCliente servicoCliente) {
                this.servicoCliente = servicoCliente;
        }

        /**
         * @brief Cadastra um novo Cliente.
         * @param requestDTO DTO com os dados do Cliente a ser cadastrado.
         * @return ResponseEntity com o DTO do Cliente cadastrado e status 201 Created.
         */
        @PostMapping
        public ResponseEntity<BaseResponse<ClienteResponseDTO>> cadastrarCliente(
                        @Valid @RequestBody ClienteRequestDTO requestDTO,
                        @RequestHeader(value = "X-Correlation-Id", required = false) String userId) {
                requestDTO.setUsuario(Optional.ofNullable(requestDTO.getUsuario())
                                .orElseGet(() -> UserRequestDTO.builder().id(UUID.fromString(userId)).build()));

                ClienteResponseDTO responseDTO = servicoCliente.cadastrarCliente(requestDTO); // Chamando o método
                                                                                              // original do
                                                                                              // serviço
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                BaseResponse.<ClienteResponseDTO>builder()
                                                .message("Cliente cadastrado com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responseDTO)
                                                .build());
        }

        /**
         * @brief Busca um Cliente por ID.
         * @param id ID do Cliente a ser buscado.
         * @return ResponseEntity com o DTO do Cliente encontrado e status 200 OK.
         */
        @GetMapping("/{id}")
        public ResponseEntity<BaseResponse<ClienteResponseDTO>> buscarClientePorId(@PathVariable UUID id) {
                ClienteResponseDTO responseDTO = servicoCliente.buscarClientePorId(id); // Chamando o método original do
                                                                                        // serviço
                return ResponseEntity.ok(
                                BaseResponse.<ClienteResponseDTO>builder()
                                                .message("Cliente encontrado.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responseDTO)
                                                .build());
        }

        /**
         * @brief Lista todos os Clientes com paginação.
         * @param pageable Objeto Pageable para configuração da paginação (ex:
         *                 ?page=0&size=10&sort=nomeCompleto,asc).
         * @return ResponseEntity com uma página de DTOs de Clientes e status 200 OK.
         */
        @GetMapping
        public ResponseEntity<BaseResponse<Page<ClienteResponseDTO>>> listarClientes(
                        @PageableDefault(size = 10, page = 0, sort = "nomeCompleto") Pageable pageable) {
                Page<ClienteResponseDTO> responsePage = servicoCliente.listarClientes(pageable); // Chamando o método
                                                                                                 // original
                                                                                                 // do serviço
                return ResponseEntity.ok(
                                BaseResponse.<Page<ClienteResponseDTO>>builder()
                                                .message("Lista de clientes recuperada com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responsePage)
                                                .build());
        }

        /**
         * @brief Atualiza os dados de um Cliente existente.
         * @param id         ID do Cliente a ser atualizado.
         * @param requestDTO DTO com os dados atualizados do Cliente.
         * @return ResponseEntity com o DTO do Cliente atualizado e status 200 OK.
         */
        @PutMapping("/{id}")
        public ResponseEntity<BaseResponse<ClienteResponseDTO>> atualizarCliente(
                        @PathVariable UUID id,
                        @Valid @RequestBody ClienteRequestDTO requestDTO) {
                ClienteResponseDTO responseDTO = servicoCliente.atualizarCliente(id, requestDTO); // Chamando o método
                                                                                                  // original
                                                                                                  // do serviço
                return ResponseEntity.ok(
                                BaseResponse.<ClienteResponseDTO>builder()
                                                .message("Cliente atualizado com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .data(responseDTO)
                                                .build());
        }

        /**
         * @brief Exclui um Cliente pelo ID.
         * @param id ID do Cliente a ser excluído.
         * @return ResponseEntity com status 204 No Content.
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<BaseResponse<Void>> excluirCliente(@PathVariable UUID id) {
                servicoCliente.excluirCliente(id); // Chamando o método original do serviço
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                                BaseResponse.<Void>builder()
                                                .message("Cliente excluído com sucesso.")
                                                .status(StatusResponse.SUCESSO)
                                                .build());
        }
}

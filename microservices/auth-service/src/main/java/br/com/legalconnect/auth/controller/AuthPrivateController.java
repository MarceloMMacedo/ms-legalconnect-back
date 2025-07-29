package br.com.legalconnect.auth.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.auth.dto.UserResponseDTO;
import br.com.legalconnect.auth.dto.UserStatusUpdateRequest;
import br.com.legalconnect.auth.service.AuthService;
import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.enums.StatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/privado/auth")
@RequiredArgsConstructor
public class AuthPrivateController {
        private static final Logger log = LoggerFactory.getLogger(AuthController.class);

        private final AuthService authService;

        /**
         * Endpoint para atualizar o status de um usuário.
         * Este endpoint está protegido e o acesso é controlado pela
         * anotação @PreAuthorize no serviço.
         * 
         * @param userId  O ID do usuário a ser atualizado.
         * @param request DTO contendo o novo status.
         * @return Resposta padronizada com DTO do usuário atualizado.
         */
        @PutMapping("/users/{userId}/status")
        public ResponseEntity<BaseResponse<UserResponseDTO>> updateUserStatus(
                        @PathVariable UUID userId,
                        @Valid @RequestBody UserStatusUpdateRequest request) {
                log.info("Requisição para atualizar status do usuário ID: {} para: {}", userId, request.getNewStatus());
                long startTime = System.currentTimeMillis();
                UserResponseDTO updatedUser = authService.updateUserStatus(userId, request.getNewStatus());
                long endTime = System.currentTimeMillis();
                log.info("Atualização de status para o usuário ID: {} processada em {} ms. Status: Sucesso",
                                userId, (endTime - startTime));
                return ResponseEntity.ok(BaseResponse.<UserResponseDTO>builder()
                                .data(updatedUser)
                                .message("Status do usuário atualizado com sucesso!")
                                .status(StatusResponse.SUCESSO)
                                .build());
        }
}

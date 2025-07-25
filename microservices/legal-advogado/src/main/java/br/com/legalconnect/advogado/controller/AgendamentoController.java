package br.com.legalconnect.advogado.controller;

import static br.com.legalconnect.enums.StatusResponse.ERRO;
import static br.com.legalconnect.enums.StatusResponse.SUCESSO;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.advogado.application.service.AgendamentoService;
import br.com.legalconnect.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

/**
 * Controller para o módulo de Agendamento de Consultas.
 * Gerencia as operações relacionadas ao agendamento e avaliação de consultas
 * entre clientes e profissionais.
 */
@RestController
@RequestMapping("/api/v1/advogados/agendamentos")
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos de consultas e avaliações")
public class AgendamentoController {

        private final AgendamentoService agendamentoService;

        @Autowired
        public AgendamentoController(AgendamentoService agendamentoService) {
                this.agendamentoService = agendamentoService;
        }

        /**
         * Realiza o agendamento de uma consulta.
         * Funcionalidade Completa: Cenário 1 (Cliente busca e agenda) e Cenário 2
         * (Advogado recebe e confirma).
         * Regras de Negócio: Verifica disponibilidade do profissional, profissional
         * deve estar ativo e usar marketplace, integração com Google Calendar, envio de
         * e-mails.
         *
         * @param profissionalId     ID do profissional.
         * @param clienteId          ID do cliente.
         * @param horarioAgendamento Horário da consulta (formato ISO).
         * @param tipoServico        Tipo de serviço agendado.
         * @return ResponseEntity indicando o sucesso do agendamento.
         */
        @Operation(summary = "Agenda uma nova consulta", description = "Permite a um cliente agendar uma consulta com um profissional, verificando disponibilidade e enviando notificações.", responses = {
                        @ApiResponse(responseCode = "201", description = "Consulta agendada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos (ex: horário indisponível)"),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado"),
                        @ApiResponse(responseCode = "409", description = "Conflito de agendamento (horário já ocupado)"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping("/agendar")
        public ResponseEntity<BaseResponse<Boolean>> agendarConsulta(
                        @Parameter(description = "ID do profissional para o agendamento") @RequestParam @NotNull UUID profissionalId,
                        @Parameter(description = "ID do cliente que está agendando") @RequestParam @NotNull UUID clienteId,
                        @Parameter(description = "Horário desejado para a consulta (formato ISO 8601, ex: 2024-07-25T10:00:00)") @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horarioAgendamento,
                        @Parameter(description = "Tipo de serviço a ser agendado (ex: 'Consulta Online', 'Reunião Presencial')") @RequestParam @NotNull String tipoServico) {

                boolean sucesso = agendamentoService.agendarConsulta(profissionalId, clienteId, horarioAgendamento,
                                tipoServico);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.<Boolean>builder()
                                                .status(SUCESSO)
                                                .message("Consulta agendada com sucesso.")
                                                .data(sucesso)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /**
         * Permite ao cliente avaliar uma consulta.
         * Funcionalidade Completa: Cenário 3 (Cliente avalia e influencia ranking).
         * Regras de Negócio: Associa avaliação ao agendamento e profissional, atualiza
         * média de avaliação.
         *
         * @param agendamentoId ID do agendamento avaliado.
         * @param clienteId     ID do cliente que avaliou.
         * @param rating        Nota da avaliação (1-5).
         * @param comentario    Comentário opcional.
         * @return ResponseEntity indicando o sucesso da avaliação.
         */
        @Operation(summary = "Avalia uma consulta", description = "Permite a um cliente fornecer uma nota e um comentário para uma consulta já realizada.", responses = {
                        @ApiResponse(responseCode = "200", description = "Consulta avaliada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos (ex: nota fora do intervalo)"),
                        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        })
        @PostMapping("/avaliar")
        public ResponseEntity<BaseResponse<Boolean>> avaliarConsulta(
                        @Parameter(description = "ID do agendamento a ser avaliado") @RequestParam @NotNull UUID agendamentoId,
                        @Parameter(description = "ID do cliente que está avaliando") @RequestParam @NotNull UUID clienteId,
                        @Parameter(description = "Nota da avaliação (1 a 5)") @RequestParam @NotNull int rating,
                        @Parameter(description = "Comentário opcional sobre a avaliação") @RequestParam(required = false) String comentario) {

                // Regra de Negócio: Rating deve estar entre 1 e 5
                if (rating < 1 || rating > 5) {
                        return ResponseEntity.badRequest().body(BaseResponse.<Boolean>builder()
                                        .status(ERRO)
                                        .message("A nota da avaliação deve ser entre 1 e 5.")
                                        .data(false)
                                        .timestamp(java.time.LocalDateTime.now())
                                        .build());
                }

                boolean sucesso = agendamentoService.avaliarConsulta(agendamentoId, clienteId, rating, comentario);
                return ResponseEntity.ok(BaseResponse.<Boolean>builder()
                                .status(SUCESSO)
                                .message("Consulta avaliada com sucesso.")
                                .data(sucesso)
                                .timestamp(java.time.LocalDateTime.now())
                                .build());
        }
}
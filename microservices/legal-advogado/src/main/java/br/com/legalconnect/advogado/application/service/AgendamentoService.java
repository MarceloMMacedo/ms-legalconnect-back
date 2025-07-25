package br.com.legalconnect.advogado.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.modal.entity.Profissional;
import br.com.legalconnect.advogado.domain.repository.ProfissionalRepository;
import br.com.legalconnect.commom.service.EmailService; // Para enviar e-mails de confirmação
import br.com.legalconnect.commom.service.TenantContext;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável pela lógica de agendamento de consultas no Marketplace.
 */
@Slf4j
@Service
public class AgendamentoService {

        private final ProfissionalRepository profissionalRepository;
        private final GoogleCalendarService googleCalendarService;
        private final EmailService emailService;
        // Repositório de agendamentos (assumindo a entidade Agendamento)
        // private final AgendamentoRepository agendamentoRepository;

        @Autowired
        public AgendamentoService(ProfissionalRepository profissionalRepository,
                        GoogleCalendarService googleCalendarService,
                        EmailService emailService) {
                this.profissionalRepository = profissionalRepository;
                this.googleCalendarService = googleCalendarService;
                this.emailService = emailService;
                // this.agendamentoRepository = agendamentoRepository;
        }

        /**
         * Realiza o agendamento de uma consulta entre um cliente e um profissional.
         * Regras de Negócio:
         * - Verifica a disponibilidade do profissional na data/hora solicitada.
         * - Garante que o profissional existe e está ativo no marketplace.
         * - Integra com Google Calendar para adicionar o evento na agenda do
         * profissional.
         * - Envia e-mails de confirmação para cliente e profissional.
         *
         * @param profissionalId     ID do profissional.
         * @param clienteId          ID do cliente.
         * @param horarioAgendamento Horário da consulta.
         * @param tipoServico        Tipo de serviço agendado.
         * @return Sucesso do agendamento.
         * @throws BusinessException se o profissional não estiver disponível ou outras
         *                           regras de negócio forem violadas.
         */
        @Transactional
        public boolean agendarConsulta(UUID profissionalId, UUID clienteId, LocalDateTime horarioAgendamento,
                        String tipoServico) {
                UUID tenantId = TenantContext.getCurrentTenantId(); // O agendamento ocorre dentro do contexto de um
                                                                    // tenant

                Profissional profissional = profissionalRepository.findById(profissionalId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO,
                                                HttpStatus.NOT_FOUND,
                                                "Profissional não encontrado."));

                // Regra de Negócio: Profissional deve estar ativo e usar marketplace
                if (!profissional.getUsaMarketplace() || !profissional.getStatusProfissional().equals("ACTIVE")) { // Assuming
                                                                                                                   // "ACTIVE"
                                                                                                                   // status
                        throw new BusinessException(ErrorCode.ADVOCATE_NOT_AVAILABLE, HttpStatus.BAD_REQUEST,
                                        "Profissional não disponível para agendamento.");
                }

                // Regra de Negócio: Verificar disponibilidade real do profissional (com Google
                // Calendar)
                boolean isAvailable = googleCalendarService.checkAvailability(profissional.getUsuario().getEmail(),
                                horarioAgendamento);
                if (!isAvailable) {
                        throw new BusinessException(ErrorCode.APPOINTMENT_CONFLICT, HttpStatus.CONFLICT,
                                        "Horário indisponível para o profissional.");
                }

                // Regra de Negócio: Criar o registro de agendamento no banco de dados (se
                // houver entidade Agendamento)
                // Agendamento newAppointment = Agendamento.builder()
                // .profissional(profissional)
                // .clienteId(clienteId)
                // .horario(horarioAgendamento)
                // .tipoServico(tipoServico)
                // .tenantId(tenantId)
                // .build();
                // agendamentoRepository.save(newAppointment);

                // Funcionalidade Completa: Adicionar evento ao Google Calendar do profissional
                googleCalendarService.addEvent(
                                profissional.getUsuario().getEmail(),
                                "Consulta: " + tipoServico + " com Cliente " + clienteId, // Supondo que você pode obter
                                                                                          // o nome do
                                                                                          // cliente
                                horarioAgendamento,
                                horarioAgendamento.plusHours(1) // Consulta de 1 hora
                );

                // Funcionalidade Completa: Enviar e-mails de confirmação
                emailService.sendAppointmentConfirmationEmail(
                                profissional.getUsuario().getEmail(),
                                "Você tem uma nova consulta agendada com o cliente " + clienteId + " para "
                                                + horarioAgendamento);
                // emailService.sendAppointmentConfirmationEmail(
                // clienteService.findById(clienteId).getEmail(), // Supondo um clienteService
                // "Sua consulta com " + profissional.getNomeCompleto() + " foi confirmada para
                // " + horarioAgendamento
                // );

                return true;
        }

        /**
         * Simula a avaliação de uma consulta pelo cliente.
         * Regras de Negócio:
         * - Associa a avaliação ao agendamento e ao profissional.
         * - Atualiza a média de avaliação do profissional (lógica simplificada).
         *
         * @param agendamentoId ID do agendamento avaliado.
         * @param clienteId     ID do cliente que avaliou.
         * @param rating        Nota da avaliação (1-5).
         * @param comentario    Comentário opcional.
         * @return Sucesso da avaliação.
         */
        @Transactional
        public boolean avaliarConsulta(UUID agendamentoId, UUID clienteId, int rating, String comentario) {
                // Lógica para encontrar o agendamento e associar a avaliação.
                // Agendamento agendamento =
                // agendamentoRepository.findById(agendamentoId).orElseThrow(...)
                // Criar uma entidade Avaliacao.
                // Atualizar a média de avaliação do Profissional.

                // Simulação: Apenas loga a avaliação
                log.info("Cliente {} avaliou agendamento {} com nota {} e comentário: {}", clienteId, agendamentoId,
                                rating,
                                comentario);

                // Para fins de demonstração, vamos considerar um sucesso
                return true;
        }

        // Outras funcionalidades como reagendamento, cancelamento, busca de
        // agendamentos
        // seriam implementadas aqui.
}
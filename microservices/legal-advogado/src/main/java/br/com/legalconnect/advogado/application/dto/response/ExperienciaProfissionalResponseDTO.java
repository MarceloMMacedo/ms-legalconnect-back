package br.com.legalconnect.advogado.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Experiência Profissional.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienciaProfissionalResponseDTO {
    private UUID id;
    private String cargo;
    private String empresa;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String descricao;
}
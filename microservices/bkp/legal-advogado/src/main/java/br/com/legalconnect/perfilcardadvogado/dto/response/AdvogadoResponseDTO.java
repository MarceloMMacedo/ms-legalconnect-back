package br.com.legalconnect.perfilcardadvogado.dto.response; // Pacote atualizado

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de resposta para a listagem pública de advogados no marketplace.
 * Corresponde à interface Advogado solicitada pelo frontend.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvogadoResponseDTO {
    private UUID id;
    private String nome;
    private String oab;
    private String fotoUrl;
    private Double avaliacao; // Média de avaliação
    private Integer numAvaliacoes; // Número de avaliações
    private String bio;
    private List<String> especialidades;
    private String localizacao; // Cidade/Estado concatenado
    private String estado;
    private String municipio;
    private Boolean verificadoOAB; // Simulação de verificação da OAB
    private String nivel; // Nível do advogado (ex: Júnior, Pleno, Sênior)
    private String formacao; // Última formação acadêmica relevante
    private MetricasDTO metricas;
    private List<ServicoDTO> servicos;
    private Boolean fazParteDePlano; // Indica se o profissional tem um plano pago (para ordenação)
    private String nomePlano; // Novo campo para o nome do plano

    /**
     * DTO aninhado para métricas de desempenho do advogado.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetricasDTO {
        private Double satisfacao;
        private Integer casosConcluidos;
    }

    /**
     * DTO aninhado para serviços oferecidos pelo advogado.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicoDTO {
        private String nome;
        private String descricao;
        private String preco;
    }
}
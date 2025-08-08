package br.com.legalconnect.depoimento.domain.enums;

/**
 * Enum para definir o status de um depoimento.
 */
public enum DepoimentoStatus {
    PENDENTE, // Depoimento enviado, aguardando revisão
    APROVADO, // Depoimento revisado e aprovado, pode ser exibido
    REPROVADO // Depoimento revisado e reprovado, não será exibido
}
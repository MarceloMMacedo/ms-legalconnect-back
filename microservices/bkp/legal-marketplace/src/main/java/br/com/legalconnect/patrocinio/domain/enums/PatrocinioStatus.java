//
// Enum para o status dos patrocinadores.
//
package br.com.legalconnect.patrocinio.domain.enums;

/**
 * Define os possíveis status de um patrocinador.
 */
public enum PatrocinioStatus {
    ACTIVE,   // Patrocinador está ativo e pode ser exibido.
    INACTIVE  // Patrocinador está inativo e não deve ser exibido.
}
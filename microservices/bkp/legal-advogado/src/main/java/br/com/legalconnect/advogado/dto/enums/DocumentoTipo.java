package br.com.legalconnect.advogado.dto.enums;

/**
 * Enumeração para os tipos de documentos de um Profissional (Advogado).
 * 
 */
public enum DocumentoTipo {
    OAB, // Ordem dos Advogados do Brasil
    RG, // Registro Geral (identidade)
    CPF, // Cadastro de Pessoas Físicas
    COMPROVANTE_ENDERECO, // Comprovante de residência
    OUTRO // Outros tipos de documentos não listados explicitamente
}
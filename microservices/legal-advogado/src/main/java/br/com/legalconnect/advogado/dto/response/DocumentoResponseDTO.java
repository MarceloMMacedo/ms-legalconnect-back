package br.com.legalconnect.advogado.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Documento.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoResponseDTO {
    private UUID id;
    private String nomeArquivo;
    private String urlS3;
    private String tipoDocumento;
}
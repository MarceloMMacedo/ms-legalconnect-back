package br.com.legalconnect.advogado.application.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
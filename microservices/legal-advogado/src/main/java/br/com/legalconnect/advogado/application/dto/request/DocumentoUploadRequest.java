package br.com.legalconnect.advogado.application.dto.request;

import br.com.legalconnect.advogado.application.dto.enums.DocumentoTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para requisição de upload de documento.
 * O campo 'tipoDocumento' foi alterado de String para o enum DocumentoTipo
 * para garantir tipagem segura e validação.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoUploadRequest {
    @NotBlank(message = "O nome do arquivo é obrigatório.")
    @Size(max = 255, message = "O nome do arquivo deve ter no máximo 255 caracteres.")
    private String nomeArquivo;

    // Alterado de String para o enum DocumentoTipo para tipagem segura
    @NotNull(message = "O tipo do documento é obrigatório.")
    private DocumentoTipo tipoDocumento;

    @NotBlank(message = "O conteúdo do arquivo em Base64 é obrigatório.")
    private String arquivoBase64;

    @NotBlank(message = "O tipo MIME do arquivo é obrigatório.")
    @Size(max = 100, message = "O tipo MIME do arquivo deve ter no máximo 100 caracteres.")
    private String mimeType;
}
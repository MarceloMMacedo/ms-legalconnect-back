//
// DTO base polimórfico para a resposta da API de patrocinadores.
//
package br.com.legalconnect.patrocinio.application.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * DTO base polimórfico para a resposta da API de patrocinadores.
 * Utiliza o campo 'tipo' como discriminador para serializar o objeto correto.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatrocinioEventoResponseDTO.class, name = "EVENTO"),
        @JsonSubTypes.Type(value = PatrocinioEscritorioResponseDTO.class, name = "ESCRITORIO"),
        @JsonSubTypes.Type(value = PatrocinioNoticiaResponseDTO.class, name = "NOTICIA")
})
@Data
@SuperBuilder
public abstract class PatrocinioResponseDTO {
    protected UUID id;
    protected String tipo;
    protected String link;
    protected String status;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
//
// DTO base polimórfico para a resposta da API de patrocinadores.
package br.com.legalconnect.patrocinio.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * DTO base polimórfico para a resposta da API de patrocinadores.
 * Utiliza o campo 'tipo' como discriminador para serializar o objeto correto.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "tipo")
@JsonSubTypes({
                @JsonSubTypes.Type(value = DestaquesEventoResponseDTO.class, name = "EVENTO"),
                @JsonSubTypes.Type(value = DestaquesEscritorioResponseDTO.class, name = "ESCRITORIO"),
                @JsonSubTypes.Type(value = DestaquesNoticiaResponseDTO.class, name = "NOTICIA")
})
@Data
@SuperBuilder
public abstract class DestaquesResponseDTO {
        protected UUID id;
        protected String tipo;
        protected String link;
        protected String status;
        protected LocalDateTime createdAt;
        protected LocalDateTime updatedAt;
}
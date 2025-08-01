//
// DTO para a requisição de criação/atualização de patrocinadores.
// A anotação @JsonTypeInfo permite que o Spring determine qual classe de DTO
// concreta deve ser usada com base no valor do campo 'tipo'.
//
package br.com.legalconnect.patrocinio.application.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * DTO base polimórfico para a requisição de criação e atualização de patrocinadores.
 * A API usará este DTO para deserializar diferentes tipos de patrocinadores.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipo"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatrocinioEventoRequestDTO.class, name = "EVENTO"),
        @JsonSubTypes.Type(value = PatrocinioEscritorioRequestDTO.class, name = "ESCRITORIO"),
        @JsonSubTypes.Type(value = PatrocinioNoticiaRequestDTO.class, name = "NOTICIA")
})
@Data
@SuperBuilder
public abstract class PatrocinioRequestDTO {
    @NotBlank(message = "O tipo de patrocínio é obrigatório.")
    protected String tipo; // EVENTO, ESCRITORIO, NOTICIA
    @NotBlank(message = "O link é obrigatório.")
    protected String link;
    protected String status;
}
//
// Entidade JPA para um patrocínio de evento.
//
package br.com.legalconnect.patrocinio.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("EVENTO")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DestaquesEvento extends DestaquesItem {
    @Column(length = 255)
    private String titulo;
    private LocalDateTime dataEvento;
    @Column(name = "imagem_url", length = 255)
    private String imagemUrl;

    @PostLoad
    public void setTipo() {
        this.tipo = "EVENTO";
    }
}
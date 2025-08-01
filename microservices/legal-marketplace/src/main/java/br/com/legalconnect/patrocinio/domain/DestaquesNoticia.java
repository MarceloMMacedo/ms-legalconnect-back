//
// Entidade JPA para um patrocínio de notícia.
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
@DiscriminatorValue("NOTICIA")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DestaquesNoticia extends DestaquesItem {
    @Column(length = 255)
    private String titulo;
    @Column(name = "imagem_url", length = 255)
    private String imagemUrl;
    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @PostLoad
    public void setTipo() {
        this.tipo = "NOTICIA";
    }
}
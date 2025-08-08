
package br.com.legalconnect.patrocinio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PostLoad;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ESCRITORIO")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DestaquesEscritorio extends DestaquesItem {
    @Column(length = 255)
    private String nome;
    @Column(length = 255)
    private String slogan;
    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @PostLoad
    public void setTipo() {
        this.tipo = "ESCRITORIO";
    }
}
package br.com.legalconnect.advogado.domain.modal.entity;

import br.com.legalconnect.common.dto.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tb_idioma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Idioma extends BaseEntity {

    @Column(name = "nome", nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo; // Ex: "pt-BR", "en-US"

    @Column(name = "nivel", length = 50)
    private String nivel; // Mapeia o enum NivelIdioma como String
}
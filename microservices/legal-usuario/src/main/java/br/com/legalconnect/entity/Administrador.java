package br.com.legalconnect.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @class Administrador
 * @brief Entidade que representa um administrador da plataforma.
 *        Estende a entidade Pessoa. Atualmente não possui campos adicionais,
 *        mas pode ser expandido.
 *        Mapeado para a tabela 'tb_administrador' que se junta a 'tb_pessoa'
 *        pela chave primária.
 */
@Entity
@Table(name = "tb_administrador")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Administrador extends Pessoa {
    private String status;

    // Atualmente, não há campos adicionais específicos para Administrador além dos
    // herdados de Pessoa.
    // Pode ser expandido no futuro com, por exemplo, nível de acesso
    // administrativo, etc.
}
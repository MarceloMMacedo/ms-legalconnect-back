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
 * @class Cliente
 * @brief Entidade que representa um cliente.
 *        Estende a entidade Pessoa. Atualmente não possui campos adicionais,
 *        mas pode ser expandido.
 *        Mapeado para a tabela 'tb_cliente' que se junta a 'tb_pessoa' pela
 *        chave primária.
 */
@Entity
@Table(name = "tb_cliente")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends Pessoa {
    private String status;
    private String tipo;

    // Atualmente, não há campos adicionais específicos para Cliente além dos
    // herdados de Pessoa.
    // Pode ser expandido no futuro com, por exemplo, histórico de preferências,
    // etc.
}
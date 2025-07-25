package br.com.legalconnect.commom.dto.response; // Assumindo um pacote common.dto.response para entidades comuns

import java.util.UUID;

import br.com.legalconnect.commom.model.Endereco.TipoEndereco; // Importar o enum TipoEndereco da entidade Endereco
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para resposta de Endereço.
 * 
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoResponseDTO {
    private UUID id;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;

    private String cep;

    private String pais;

    private TipoEndereco tipoEndereco;
}
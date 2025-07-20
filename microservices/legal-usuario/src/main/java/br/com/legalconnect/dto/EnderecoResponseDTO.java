package br.com.legalconnect.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.legalconnect.entity.Endereco.TipoEndereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @class EnderecoResponseDTO
 * @brief DTO para respostas de Endereco.
 */
@Data
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
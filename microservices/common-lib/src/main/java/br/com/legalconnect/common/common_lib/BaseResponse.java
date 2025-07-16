package br.com.legalconnect.common.common_lib;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @class BaseResponse
 * @brief Classe genérica base para todas as respostas da API.
 *
 *        Padroniza a estrutura das respostas, incluindo campos para código de
 *        erro,
 *        mensagem de erro e o payload de dados de sucesso.
 * @tparam T O tipo do payload de dados de sucesso.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    protected Integer codigoErro; // Código de erro padronizado, se houver
    protected String mensagemErro; // Mensagem descritiva do erro, se houver
    private T data; // Payload da resposta (dados de sucesso)

    public BaseResponse(T data) {
        this.data = data;
    }

    public BaseResponse(Integer codigoErro, String mensagemErro) {
        this.codigoErro = codigoErro;
        this.mensagemErro = mensagemErro;
    }
}
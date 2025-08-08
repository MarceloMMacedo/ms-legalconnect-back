package br.com.legalconnect.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * @class BusinessException
 * @brief Exceção personalizada para representar erros de negócio na aplicação.
 *
 *        Esta exceção é lançada quando uma regra de negócio não é satisfeita.
 *        Ela encapsula um {@link ErrorCode} e, opcionalmente, argumentos para
 *        formatar
 *        a mensagem de erro, além de um status HTTP para a resposta da API.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode; // O código de erro padronizado
    private final String[] args; // Argumentos para formatar a mensagem do erro
    private final HttpStatus httpStatus; // Status HTTP associado a esta exceção

    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String args) {
        super(args);
        this.errorCode = errorCode;
        this.args = new String[] { args, errorCode.getMessage() };
        this.httpStatus = httpStatus;
    }

    /**
     * Construtor para BusinessException com um ErrorCode e status HTTP padrão
     * (BAD_REQUEST).
     *
     * @param errorCode O código de erro que define o tipo de exceção de negócio.
     * @param args      Argumentos opcionais para formatar a mensagem do erro.
     */
    public BusinessException(ErrorCode errorCode, String... args) {
        this(errorCode, HttpStatus.BAD_REQUEST, args); // Por padrão, erros de negócio são BAD_REQUEST
    }

    /**
     * Construtor para BusinessException com um ErrorCode e um status HTTP
     * específico.
     *
     * @param errorCode  O código de erro que define o tipo de exceção de negócio.
     * @param httpStatus O status HTTP a ser retornado na resposta da API.
     * @param args       Argumentos opcionais para formatar a mensagem do erro.
     */
    public BusinessException(ErrorCode errorCode, HttpStatus httpStatus, String... args) {
        super(errorCode.getFormattedMessage(args)); // Define a mensagem da exceção usando a mensagem formatada do
                                                    // ErrorCode
        this.errorCode = errorCode;
        this.args = args;
        this.httpStatus = httpStatus;

    }

    /**
     * @brief Retorna a mensagem de erro formatada.
     * @return A mensagem de erro formatada.
     */
    @Override
    public String getMessage() {
        return errorCode.getFormattedMessage(args);
    }

    /**
     * @brief Retorna o código de erro.
     * @return O código de erro.
     */
    public String getCode() {
        return errorCode.getCode();
    }
}

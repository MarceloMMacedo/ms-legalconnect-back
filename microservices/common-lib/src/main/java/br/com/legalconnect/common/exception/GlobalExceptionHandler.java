package br.com.legalconnect.common.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.enums.StatusResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @class GlobalExceptionHandler
 * @brief Manipulador global de exceções para a aplicação Spring Boot.
 *
 *        Esta classe intercepta exceções lançadas em qualquer parte da
 *        aplicação
 *        e as transforma em respostas de erro padronizadas da API, utilizando
 *        {@link BaseResponse}.
 *        Lida com {@link BusinessException} e exceções de validação do Spring,
 *        além de capturar exceções genéricas.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

        /**
         * @brief Manipula exceções de negócio personalizadas
         *        ({@link BusinessException}).
         *
         *        Retorna uma resposta de erro com o status HTTP e a mensagem definidos
         *        na BusinessException.
         *
         * @param ex      A exceção de negócio lançada.
         * @param request A requisição web atual.
         * @return Uma {@link ResponseEntity} contendo a {@link BaseResponse} de erro.
         */
        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<BaseResponse> handleBusinessException(BusinessException ex, WebRequest request) {
                log.warn("Business Exception: {} - Path: {}", ex.getMessage(), request.getDescription(false));

                BaseResponse errorResponse = BaseResponse.builder()
                                .status(StatusResponse.ERRO)
                                .message(ex.getMessage())
                                .errors(List.of(ex.getCode())) // Adiciona o código de erro como parte dos erros
                                .timestamp(LocalDateTime.now())
                                .errors(Arrays.asList(ex.getArgs())) // Adiciona os argumentos da exceção como parte dos
                                .build();

                return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
        }

        /**
         * @brief Manipula exceções de validação de argumentos de método
         *        ({@link MethodArgumentNotValidException}).
         *
         *        Ocorre quando a validação de um DTO de entrada falha (ex:
         *        campos @NotNull, @Size).
         *        Coleta todos os erros de campo e os retorna em uma lista na resposta.
         *
         * @param ex      A exceção de validação.
         * @param request A requisição web atual.
         * @return Uma {@link ResponseEntity} contendo a {@link BaseResponse} com os
         *         erros de validação.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<BaseResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
                        WebRequest request) {
                List<String> errors = ex.getBindingResult().getAllErrors().stream()
                                .map(error -> {
                                        String fieldName = (error instanceof FieldError)
                                                        ? ((FieldError) error).getField()
                                                        : error.getObjectName();
                                        String errorMessage = error.getDefaultMessage();
                                        return String.format("Campo '%s': %s", fieldName, errorMessage);
                                })
                                .collect(Collectors.toList());

                log.warn("Validation Exception: {} - Errors: {} - Path: {}", ex.getMessage(), errors,
                                request.getDescription(false));

                BaseResponse errorResponse = BaseResponse.builder()
                                .status(StatusResponse.ERRO)
                                .message(ErrorCode.VALIDATION_ERROR.getMessage())
                                .errors(errors)
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        /**
         * @brief Manipula todas as outras exceções não tratadas especificamente.
         *
         *        Captura qualquer {@link Exception} genérica, registra o erro e retorna
         *        uma resposta de erro genérica com status HTTP 500 (Internal Server
         *        Error).
         *
         * @param ex      A exceção genérica.
         * @param request A requisição web atual.
         * @return Uma {@link ResponseEntity} contendo a {@link BaseResponse} de erro
         *         genérico.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<BaseResponse> handleGlobalException(Exception ex, WebRequest request) {
                log.error("Unhandled Exception: {} - Path: {}", ex.getMessage(), request.getDescription(false), ex);

                BaseResponse errorResponse = BaseResponse.builder()
                                .status(StatusResponse.ERRO)
                                .message(ErrorCode.GENERIC_ERROR.getMessage())
                                .errors(List.of(ErrorCode.GENERIC_ERROR.getCode())) // Adiciona o código de erro
                                                                                    // genérico
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<BaseResponse> handleGlobalException(AccessDeniedException ex, WebRequest request) {
                log.error("Unhandled Exception: {} - Path: {}", ex.getMessage(), request.getDescription(false), ex);

                BaseResponse errorResponse = BaseResponse.builder()
                                .status(StatusResponse.ERRO)
                                .message(ErrorCode.UNAUTHORIZED_ACCESS.getMessage())
                                .errors(List.of(ErrorCode.UNAUTHORIZED_ACCESS.getCode())) // Adiciona o código de erro
                                                                                          // genérico
                                .timestamp(LocalDateTime.now())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
}
package br.com.legalconnect.gateway.config.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import br.com.legalconnect.gateway.config.enums.StatusResponse;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BusinessException.class)
        public Mono<ResponseEntity<BaseResponse>> handleBusinessException(
                        BusinessException ex,
                        ServerWebExchange exchange) {

                BaseResponse response = BaseResponse.builder()
                                .status(StatusResponse.ERRO)
                                .message(ex.getMessage())
                                .build();

                return Mono.just(ResponseEntity
                                .status(ex.getHttpStatus())
                                .body(response));
        }
}
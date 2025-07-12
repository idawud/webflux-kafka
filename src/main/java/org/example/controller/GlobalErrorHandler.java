package org.example.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Order(-2)  // Make sure this runs before default handlers
public class GlobalErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<Void> handleNotFound(ResponseStatusException ex, ServerWebExchange exchange) {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String errorJson = "{\"error\": \"Route not found\"}";
            byte[] bytes = errorJson.getBytes();
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        }
        // For other exceptions, just propagate
        return Mono.error(ex);
    }
}

package org.example.controller;

import org.example.model.Event;
import org.example.service.KafkaMessageListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {
    private final KafkaMessageListener kafkaMessageListener;
    private final Sinks.Many<Event> sink = Sinks.many().replay().all();

    public MessageController(KafkaMessageListener kafkaMessageListener) {
        this.kafkaMessageListener = kafkaMessageListener;
        this.kafkaMessageListener.setListener(event -> {
            System.out.println("[Received message] : " + event);
            sink.tryEmitNext(event);
        });
    }

    // SSE endpoint streaming all past + new messages
    @GetMapping(value = "/messages/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> streamMessages() {
        System.out.println("Received request for SSE messages");
        return sink.asFlux();
    }

    @GetMapping("/messages")
    public Mono<List<Event>> getMessages() {
        System.out.println("Received request for messages");
        return Mono.defer(() -> {
            var list = new ArrayList<>(kafkaMessageListener.getMessages());
            return Mono.just(list);
        });
    }

    @GetMapping("/ping")
    public Mono<String> ping() {
        System.out.println("Received ping request");
        return Mono.just("pong");
    }
}

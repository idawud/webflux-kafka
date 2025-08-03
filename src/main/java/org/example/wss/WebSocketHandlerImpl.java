package org.example.wss;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandlerImpl implements WebSocketHandler {
    private static final ObjectMapper json = new ObjectMapper();
    private final CopyOnWriteArrayList<WebSocketSession> activeSessions = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        activeSessions.add(session);
        System.out.printf("%s session opened, active sessions: [ count=%s,  %s ] %n",
                session.getId(),
                activeSessions.size(),
                activeSessions.stream().map(WebSocketSession::getId).toList()
        );

        // Handle incoming messages
        Flux<WebSocketMessage> outputMessages = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(message -> handleMessage(message, session));
        try {
            Mono<WebSocketMessage> welcome = Mono.just(session.textMessage(WsMessage.welcome("server").toJson()));

            // Handle session closure
            session.closeStatus().doFinally(signal -> {
                activeSessions.remove(session);
                System.out.printf("%s session closed,  active sessions: [ count=%s, %s ] %n",
                        session.getId(),
                        activeSessions.size(),
                        activeSessions.stream().map(WebSocketSession::getId).toList()
                );
            }).subscribe();

            return session.send(Flux.concat(welcome, outputMessages));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Publisher<? extends WebSocketMessage> handleMessage(String message, WebSocketSession session) {
        try {
            WsMessage receivedMessage = json.readValue(message, WsMessage.class);
            System.out.printf("Received: [%s] , from: [%s], on session: %s%n",
                    receivedMessage.content(),
                    receivedMessage.sender(),
                    session.getId()
            );

            // Broadcast the message to all active sessions except the sender
            String broadcastJson = WsMessage.broadcast(
                    String.format("%s from %s ", receivedMessage.content(), session.getId()),
                    receivedMessage.sender()
            ).toJson();
            activeSessions.forEach(s -> {
                if (!s.equals(session)) { // Don't echo back to sender
                    s.send(Mono.just(s.textMessage(broadcastJson))).subscribe();
                }
            });

            // Echo back to the sender
            return Mono.just(session.textMessage(WsMessage.response("Message received", "server").toJson()));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
package org.example.wss;

import com.fasterxml.jackson.databind.ObjectMapper;

public record WsMessage(String type, String content, String sender) {
    private static final ObjectMapper json = new ObjectMapper();

    public static WsMessage welcome(String sender) {
        return new WsMessage(Type.WELCOME.name(), "Welcome to the WebSocket server!", sender);
    }

    public static WsMessage broadcast(String content, String sender) {
        return new WsMessage(Type.BROADCAST.name(), content, sender);
    }

    public static WsMessage response(String content, String sender) {
        return new WsMessage(Type.RESPONSE.name(), content, sender);
    }

    public String toJson() {
        try {
            return json.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Error converting WsMessage to JSON", e);
        }
    }

    public enum Type {
        WELCOME, BROADCAST, RESPONSE
    }
}

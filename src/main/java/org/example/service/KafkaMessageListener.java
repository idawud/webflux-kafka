package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.model.Event;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaMessageListener {

    private final CopyOnWriteArrayList<Event> messages = new CopyOnWriteArrayList<>();
    private OnMessageListener listener;
    //   private final Sinks.Many<String> sink = Sinks.many().replay().all();

    @PostConstruct
    public void init() {
        System.out.println("KafkaMessageListener initialized");
    }

    public void setListener(OnMessageListener listener) {
        this.listener = listener;
    }

    @KafkaListener(topics = "demo-topic", groupId = "webflux-consumer-group", containerFactory = "eventKafkaListenerContainerFactory")
    public void listen(Event message) {
        messages.add(message);
        if (listener != null) {
            listener.onMessage(message);
        }
    }

    public CopyOnWriteArrayList<Event> getMessages() {
        return messages;
    }

    public interface OnMessageListener {
        void onMessage(Event message);
    }
}

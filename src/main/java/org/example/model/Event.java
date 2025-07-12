package org.example.model;

//{"index": 9, "msg": "Hello Kafka #9", "timestamp": 1752343909.5354762}
public record Event(int index, String msg, String timestamp) {
    @Override
    public String toString() {
        return "Event{" + "index=" + index + ", msg='" + msg + '\'' + ", timestamp=" + timestamp + '}';
    }
}

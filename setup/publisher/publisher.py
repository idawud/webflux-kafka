from kafka import KafkaProducer
import time
from datetime import datetime
import json
import os

KAFKA_SERVER = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")

producer = KafkaProducer(
    bootstrap_servers=KAFKA_SERVER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

topic = 'demo-topic'

i = 0
while i < 10:
    timestamp = time.time()
    iso_time = datetime.fromtimestamp(timestamp).isoformat()
    message = {'index': i, 'msg': f'Hello Kafka #{i}', 'timestamp': iso_time}
    producer.send(topic, message)
    print(f"Sent: {message}")
    time.sleep(1)
    i += 1

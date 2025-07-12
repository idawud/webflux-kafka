FROM python:3.11-slim

WORKDIR /app

COPY publisher.py .

RUN pip install kafka-python

CMD ["python", "publisher.py"]

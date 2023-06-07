FROM python:3.8-slim-buster

WORKDIR /app

COPY API /app/API

WORKDIR /app/API

RUN pip install -r requirements.txt

EXPOSE 5019/tcp

CMD ["python3", "app.py"]

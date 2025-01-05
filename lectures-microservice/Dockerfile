FROM python:3.11-slim

ENV USER=docker
ENV GROUPNAME=$USER
ENV UID=12345
ENV GID=23456

RUN addgroup --gid $GID $GROUPNAME \
    && adduser --disabled-password --gecos "" --home "/app" --ingroup $GROUPNAME --no-create-home --uid $UID $USER

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN mkdir -p /app/logs && chown -R $USER:$GROUPNAME /app

ENV PYTHONPATH=/app

USER $USER

EXPOSE 8000

CMD python main.py

package br.com.camila.motor.messaging;

import lombok.Getter;

/**
 * Classe utilitária para definir as configurações de recebimento de eventos de acordo com o padrão de mensagem definido
 * no projeto.
 *
 * Padrão de routingKey de eventos: "producer.chave-do-evento.finished.event" Padrão de exchange de eventos:
 * "producer.events.exchange" Padrão de queue para eventos: "consumer.chave-do-evento.reply.queue"
 */
@Getter
public class EventInbox {

    private static final String PROJECT = "proposta";

    private final String queue;

    private final String exchange;

    private final String routingKey;

    public EventInbox(String routingKey) {
        this.routingKey = routingKey;
        this.exchange = String.format("%s.events.exchange", prefix(routingKey));
        this.queue = String.format("%s.%s.reply.queue", PROJECT, message(routingKey));
    }

    private String message(String routingKey) {
        return routingKey.split("\\.")[1];
    }

    private String prefix(String routingKey) {
        return routingKey.split("\\.")[0];
    }
}

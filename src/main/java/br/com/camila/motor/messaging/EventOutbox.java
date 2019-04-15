package br.com.camila.motor.messaging;

import lombok.Getter;

/**
 * Classe utilitária para definir as configurações de envio de eventos de acordo com o padrão de mensagem definido no
 * projeto.
 *
 * Padrão de routingKey de eventos: "producer.chave-do-evento.finished.event" Padrão de exchange de eventos:
 * "producer.events.exchange"
 */
@Getter
public class EventOutbox {

    private final String exchange;

    private final String routingKey;

    public EventOutbox(String routingKey) {
        this.routingKey = routingKey;
        this.exchange = String.format("%s.events.exchange", prefix(routingKey));
    }

    private String prefix(String routingKey) {
        return routingKey.split("\\.")[0];
    }
}

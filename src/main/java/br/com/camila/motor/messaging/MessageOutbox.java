package br.com.camila.motor.messaging;

import lombok.Getter;

/**
 * Classe utilitária para definir as configurações de envio de mensagens de acordo com o padrão de mensagem definido no
 * projeto.
 *
 * Padrão de routingKey de eventos: "consumer.chave-do-evento.message" Padrão de exchange de eventos:
 * "consumer.exchange"
 */
@Getter
public class MessageOutbox {

    private final String exchange;

    private final String routingKey;

    public MessageOutbox(String routingKey) {
        this.routingKey = routingKey;
        this.exchange = String.format("%s.exchange", prefix(routingKey));
    }

    private String prefix(String routingKey) {
        return routingKey.split("\\.")[0];
    }
}

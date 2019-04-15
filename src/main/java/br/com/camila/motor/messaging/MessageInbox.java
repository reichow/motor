package br.com.camila.motor.messaging;

import lombok.Getter;

/**
 * Classe utilitária para definir as configurações de recebimento de mensagens de acordo com o padrão de mensagem
 * definido no projeto.
 *
 * Padrão de routingKey de eventos: "consumer.chave-da-mensagem.message" Padrão de exchange de eventos:
 * "consumer.exchange" Padrão de queue para eventos: "consumer.chave-da-mensagem.reply.queue"
 */
@Getter
public class MessageInbox {

    private final String queue;

    private final String exchange;

    private final String routingKey;

    public MessageInbox(String routingKey) {
        this.routingKey = routingKey;
        this.exchange = String.format("%s.exchange", prefix(routingKey));
        this.queue = String.format("%s.%s.queue", prefix(routingKey), message(routingKey));
    }

    private String message(String routingKey) {
        return routingKey.split("\\.")[1];
    }

    private String prefix(String routingKey) {
        return routingKey.split("\\.")[0];
    }

}

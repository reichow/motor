package br.com.camila.motor.messaging;

/**
 * Interface para definição de nomes de exchanges, queues e routing keys.
 */
public interface Messaging {

    MessageInbox ANALISAR_PRE_MOTOR = new MessageInbox("motor.analisar-pre-proposta-motor.message");
    MessageInbox ANALISAR_POS_MOTOR = new MessageInbox("motor.analisar-pos-proposta-motor.message");

    MessageOutbox PRE_ANALISADA = new MessageOutbox("proposta.pre-proposta-analisada.message");
    MessageOutbox POS_ANALISADA = new MessageOutbox("proposta.pos-proposta-analisada.message");

    String EXCHANGE = "motor.exchange";
    String EXCHANGE_EVENTS = "motor.events.exchange";

    String QUEUE_ANALISAR_PRE = "motor.analisar-pre-proposta.queue";
    String QUEUE_ANALISAR_POS = "motor.analisar-pos-proposta.queue";
}

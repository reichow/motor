package br.com.camila.motor.messaging;

/**
 * Interface para definição de nomes de exchanges, queues e routing keys.
 */
public interface Messaging {

    //msg recebida da api proposta/sm
    MessageInbox ANALISAR_PRE_PROPOSTA_MOTOR = new MessageInbox("motor.analisar-pre-proposta-motor.message");
    MessageInbox ANALISAR_POS_PROPOSTA_MOTOR = new MessageInbox("motor.analisar-pos-proposta-motor.message");

    //msg enviada para api proposta/sm
    MessageOutbox PRE_PROPOSTA_ANALISADA = new MessageOutbox("proposta.pre-proposta-analisada.message");
    MessageOutbox POS_PROPOSTA_ANALISADA = new MessageOutbox("proposta.pos-proposta-analisada.message");

    //exchange
    String EXCHANGE = "motor.exchange";
    String EXCHANGE_EVENTS = "motor.events.exchange";

    //filas
    String QUEUE_ANALISAR_PRE_PROPOSTA = "motor.analisar-pre-proposta.queue";
    String QUEUE_ANALISAR_POS_PROPOSTA = "motor.analisar-pos-proposta.queue";
}

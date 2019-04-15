package br.com.camila.motor.messaging;

/**
 * Interface para definição de nomes de exchanges, queues e routing keys.
 */
public interface Messaging {

//    EventOutbox PRE_PROPOSTA_ANALISADA_MOTOR = new EventOutbox("motor.analisar-pre-proposta.success.event");
//    EventOutbox POS_PROPOSTA_ANALISADA_MOTOR = new EventOutbox("motor.analisar-pos-proposta-motor.success.event");
//
//    EventOutbox ANALISE_PRE_PROPOSTA_MOTOR_ERROR = new EventOutbox("motor.analise-pre-proposta.error.event");
//    EventOutbox ANALISE_POS_PROPOSTA_MOTOR_ERROR = new EventOutbox("motor.analise-pos-proposta.error.event");

    MessageInbox ANALISAR_PRE_PROPOSTA_MOTOR = new MessageInbox("motor.analisar-pre-proposta-motor.message");
    MessageInbox ANALISAR_POS_PROPOSTA_MOTOR = new MessageInbox("motor.analisar-pos-proposta-motor.message");

    MessageOutbox PRE_PROPOSTA_ANALISADA = new MessageOutbox("proposta.pre-proposta-analisada.message");

    String EXCHANGE = "motor.exchange";
    String EXCHANGE_EVENTS = "motor.events.exchange";

    String QUEUE_ANALISAR_PRE_PROPOSTA = "motor.analisar-pre-proposta.queue";
    String QUEUE_ANALISAR_POS_PROPOSTA = "motor.analisar-pos-proposta.queue";
}

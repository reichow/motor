package br.com.camila.motor.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.message.AnalisarPosPropostaMotorMessage;
import br.com.camila.motor.message.PosPropostaAnalisadaMessage;
import br.com.camila.motor.messaging.Messaging;
import lombok.extern.slf4j.Slf4j;

@Component
@RabbitListener(queues = Messaging.QUEUE_ANALISAR_POS_PROPOSTA)
@Slf4j
public class AnalisarPosPropostaListener {

    @Autowired
    @EventTemplate
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    void receive(
        @Payload final AnalisarPosPropostaMotorMessage message) {

        log.info("Mensagem: {}", message);

        PosPropostaAnalisadaMessage pre = PosPropostaAnalisadaMessage.builder().numeroProposta(message.getNumeroProposta()).estado("APROVADO_PRE").build();

        rabbitTemplate.convertAndSend(
            Messaging.POS_PROPOSTA_ANALISADA.getExchange(),
            Messaging.POS_PROPOSTA_ANALISADA.getRoutingKey(),
            pre);
    }
}
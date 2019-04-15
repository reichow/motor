package br.com.camila.motor.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.message.AnalisarPrePropostaMotorMessage;
import br.com.camila.motor.message.PrePropostaAnalisadaMessage;
import br.com.camila.motor.messaging.Messaging;
import lombok.extern.slf4j.Slf4j;

@Component
@RabbitListener(queues = Messaging.QUEUE_ANALISAR_PRE_PROPOSTA)
@Slf4j
public class AnalisarPrePropostaListener {

    @Autowired
    @EventTemplate
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    void receive(
        @Payload final AnalisarPrePropostaMotorMessage message) {

        log.info("Mensagem: {}", message);

        PrePropostaAnalisadaMessage pre = PrePropostaAnalisadaMessage.builder().numeroProposta(message.getNumeroProposta()).estado("APROVADO_PRE").build();

        rabbitTemplate.convertAndSend(
            Messaging.PRE_PROPOSTA_ANALISADA.getExchange(),
            Messaging.PRE_PROPOSTA_ANALISADA.getRoutingKey(),
            pre);
    }
}

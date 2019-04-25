package br.com.camila.motor.listener;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.message.AnalisarPosMessage;
import br.com.camila.motor.message.PosAnalisadaMessage;
import br.com.camila.motor.messaging.Messaging;
import lombok.extern.slf4j.Slf4j;

@Component
@RabbitListener(queues = Messaging.QUEUE_ANALISAR_POS)
@Slf4j
public class AnalisarPosListener {

    @Autowired
    @EventTemplate
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    void receive(@Payload final AnalisarPosMessage message) {

        log.info("Mensagem: {}", message);

        PosAnalisadaMessage pre = PosAnalisadaMessage.builder()
            .numeroProposta(message.getNumeroProposta())
            .estado(definirEstado())
            .proposta(message.getProposta()).build();

        rabbitTemplate.convertAndSend(
            Messaging.POS_ANALISADA.getExchange(),
            Messaging.POS_ANALISADA.getRoutingKey(),
            pre);
    }

    private String definirEstado() {
        Random random = new Random();
        Estados estado = Estados.values()[random.nextInt(Estados.values().length)];
        return estado.toString();
    }

    enum Estados {
        APROVADO_POS,
        NEGADO_POS
    }
}

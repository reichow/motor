package br.com.camila.motor.listener;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.message.AnalisarPrePropostaMcMotorMessage;
import br.com.camila.motor.message.PrePropostaMcAnalisadaMessage;
import br.com.camila.motor.messaging.Messaging;
import lombok.extern.slf4j.Slf4j;

@Component
@RabbitListener(queues = Messaging.QUEUE_ANALISAR_PRE_PROPOSTA_MC)
@Slf4j
public class AnalisarPrePropostaMcListener {

    @Autowired
    @EventTemplate
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    void receive(@Payload final AnalisarPrePropostaMcMotorMessage message) {

        log.info("Mensagem: {}", message);

        PrePropostaMcAnalisadaMessage pre = PrePropostaMcAnalisadaMessage.builder()
            .numeroProposta(message.getNumeroProposta())
            .estado(definirEstado()).build();

        rabbitTemplate.convertAndSend(
            Messaging.PRE_PROPOSTA_MC_ANALISADA.getExchange(),
            Messaging.PRE_PROPOSTA_MC_ANALISADA.getRoutingKey(),
            pre);
    }

    private String definirEstado() {
        Random random = new Random();
        Estados estado = Estados.values()[random.nextInt(Estados.values().length)];
        return estado.toString();
    }

    enum Estados {
        APROVADO_PRE,
        NEGADO_PRE,
        PENDENTE_PRE
    }
}

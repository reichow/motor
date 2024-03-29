package br.com.camila.motor.listener;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.domain.Tipo;
import br.com.camila.motor.message.AnalisarPreMessage;
import br.com.camila.motor.message.PreAnalisadaMessage;
import br.com.camila.motor.messaging.Messaging;
import lombok.extern.slf4j.Slf4j;

@Component
@RabbitListener(queues = Messaging.QUEUE_ANALISAR_PRE)
@Slf4j
public class AnalisarPreListener {

    @Autowired
    @EventTemplate
    private RabbitTemplate rabbitTemplate;

    @RabbitHandler
    void receive(@Payload final AnalisarPreMessage message) {

        log.info("Mensagem: {}", message);

        PreAnalisadaMessage pre = PreAnalisadaMessage.builder()
            .numeroProposta(message.getNumeroProposta())
            .estado(definirEstado(message.getProposta()))
            .proposta(message.getProposta()).build();

        rabbitTemplate.convertAndSend(
            Messaging.PRE_ANALISADA.getExchange(),
            Messaging.PRE_ANALISADA.getRoutingKey(),
            pre);
    }

    private String definirEstado(Tipo proposta) {
        Random random = new Random();

        if (proposta.equals(Tipo.CARTAO_A)) {
            Estados estado = Estados.values()[random.nextInt(Estados.values().length)];
            return estado.toString();
        }

        if (proposta.equals(Tipo.CARTAO_B)) {
            EstadosMC estado = EstadosMC.values()[random.nextInt(EstadosMC.values().length)];
            return estado.toString();
        }

        return null;
    }

    enum Estados {
        APROVADO_PRE,
        NEGADO_PRE
    }

    enum EstadosMC {
        APROVADO_PRE,
        NEGADO_PRE,
        PENDENTE_PRE
    }
}

package br.com.camila.motor.listener;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.domain.TipoProposta;
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
    void receive(@Payload final AnalisarPrePropostaMotorMessage message) {

        log.info("Mensagem: {}", message);

        PrePropostaAnalisadaMessage pre = PrePropostaAnalisadaMessage.builder()
            .numeroProposta(message.getNumeroProposta())
            .estado(definirEstado(message.getProposta()))
            .proposta(message.getProposta()).build();

        rabbitTemplate.convertAndSend(
            Messaging.PRE_PROPOSTA_ANALISADA.getExchange(),
            Messaging.PRE_PROPOSTA_ANALISADA.getRoutingKey(),
            pre);
    }

    private String definirEstado(TipoProposta proposta) {
        Random random = new Random();

        if (proposta.equals(TipoProposta.CONTRATACAO_CCR)) {
            Estados estado = Estados.values()[random.nextInt(Estados.values().length)];
            return estado.toString();
        }

        if (proposta.equals(TipoProposta.CONTRATACAO_MC)) {
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

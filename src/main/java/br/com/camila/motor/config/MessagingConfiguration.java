package br.com.camila.motor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.camila.motor.annotation.RabbitEnabled;
import br.com.camila.motor.messaging.Messaging;

@Configuration
@RabbitEnabled
public class MessagingConfiguration {

    /**
     * Exchanges
     **/

    @Bean
    TopicExchange motorExchange() {
        return new TopicExchange(Messaging.EXCHANGE);
    }

    /**
     * Queues
     **/

    @Bean
    Queue analisarPrePropostaQueue() {
        return new Queue(Messaging.QUEUE_ANALISAR_PRE);
    }

    @Bean
    Queue analisarPosPropostaQueue() {
        return new Queue(Messaging.QUEUE_ANALISAR_POS);
    }

    /**
     * Bindings
     **/

    @Bean
    Binding analisarPrePropostaQueueToMotorExchangeBinder() {
        return BindingBuilder.bind(analisarPrePropostaQueue())
            .to(motorExchange())
            .with(Messaging.ANALISAR_PRE_MOTOR.getRoutingKey());
    }

    @Bean
    Binding analisarPosPropostaQueueToMotorExchangeBinder() {
        return BindingBuilder.bind(analisarPosPropostaQueue())
            .to(motorExchange())
            .with(Messaging.ANALISAR_POS_MOTOR.getRoutingKey());
    }
}

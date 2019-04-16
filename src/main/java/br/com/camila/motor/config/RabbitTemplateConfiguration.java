package br.com.camila.motor.config;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.camila.motor.annotation.EventTemplate;
import br.com.camila.motor.annotation.RabbitEnabled;
import br.com.camila.motor.domain.TipoProposta;
import br.com.camila.motor.interceptor.HeaderMessageInterceptor;
import br.com.camila.motor.interceptor.TraceMessageInterceptor;
import br.com.camila.motor.message.AnalisarPosPropostaMotorMessage;
import br.com.camila.motor.message.AnalisarPrePropostaMcMotorMessage;
import br.com.camila.motor.message.AnalisarPrePropostaMotorMessage;
import br.com.camila.motor.message.PosPropostaAnalisadaMessage;
import br.com.camila.motor.message.PrePropostaAnalisadaMessage;
import br.com.camila.motor.message.PrePropostaMcAnalisadaMessage;
import br.com.camila.motor.messaging.MessageOutbox;
import br.com.camila.motor.messaging.Messaging;

@Configuration
@RabbitEnabled
public class RabbitTemplateConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private HeaderMessageInterceptor headerMessageInterceptor;

    @Autowired
    private TraceMessageInterceptor traceMessageInterceptor;

    @Bean
    Jackson2JsonMessageConverter objectToJsonMessageConverter() {

        final Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setClassMapper(jsonClassMapper());
        return converter;
    }

    @Bean
    DefaultClassMapper jsonClassMapper() {

        final Map<String, Class<?>> mapping = new HashMap<>();
        asList(
            AnalisarPrePropostaMotorMessage.class,
            PrePropostaAnalisadaMessage.class,
            AnalisarPosPropostaMotorMessage.class,
            PosPropostaAnalisadaMessage.class,
            AnalisarPrePropostaMcMotorMessage.class,
            PrePropostaMcAnalisadaMessage.class,
            TipoProposta.class
        )
            .forEach(clazz -> mapping.put(clazz.getSimpleName(), clazz));

        final DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(mapping);
        return classMapper;
    }

    /**
     * Cria uma instância de RabbitTemplate com o conversor de json e as informações do binder.
     */
    private RabbitTemplate createRabbitTemplate(final MessageOutbox message) {

        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setBeforePublishPostProcessors(headerMessageInterceptor, traceMessageInterceptor);
        rabbitTemplate.setMessageConverter(objectToJsonMessageConverter());

        if (nonNull(message)) {
            rabbitTemplate.setExchange(message.getExchange());
            rabbitTemplate.setRoutingKey(message.getRoutingKey());
        }

        return rabbitTemplate;
    }

    /**
     * Rabbit Templates
     **/

    @Bean
    RabbitTemplate rabbitTemplate() {
        return createRabbitTemplate(null);
    }

    @Bean
    @EventTemplate
    RabbitTemplate eventTemplate() {

        final RabbitTemplate rabbitTemplate = createRabbitTemplate(null);
        rabbitTemplate.setExchange(Messaging.EXCHANGE_EVENTS);
        return rabbitTemplate;
    }
}

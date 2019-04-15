package br.com.camila.motor.interceptor;

import static java.lang.String.format;
import static java.time.OffsetDateTime.now;
import static java.util.Optional.ofNullable;
import static org.springframework.amqp.core.MessageBuilder.withBody;
import static org.springframework.amqp.support.converter.DefaultClassMapper.DEFAULT_CLASSID_FIELD_NAME;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.Base64;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.camila.motor.annotation.EventTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;

@Component
public class ListenerExceptionInterceptor implements ThrowsAdvice {

    private final ObjectMapper objectMapper;

    @Autowired
    @EventTemplate
    private RabbitTemplate rabbitTemplate;

    public ListenerExceptionInterceptor(@Autowired final ObjectMapper objectMapper) {

        this.objectMapper = objectMapper.copy();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Assíncrono e invocado automaticamente pelo Spring Advice Chain quando ocorrer qualquer exceção no contexto
     * avaliado.
     *
     * <br /><br /> <em>Obs.: anotação {@link SneakyThrows} é utilizada pois não é necessário tratamento de erros
     * aqui.</em>
     *
     * @see #afterThrowing(Message, Throwable)
     */
    @Async
    @SneakyThrows
    public void afterThrowing(final Method method, final Object[] args, final Object target, final Throwable th) {

        final org.springframework.amqp.core.Message message = (org.springframework.amqp.core.Message) args[1];
        afterThrowing(message, th);
    }

    /**
     * Assíncrono e invocado manualmente. Cria e propaga um evento de erro, no padrão do projeto de GFE, com base em
     * <code>message</code> e <code>th</code>. <br
     * </ul>
     *
     * <em>Obs.: anotação {@link SneakyThrows} é utilizada pois não é necessário tratamento de erros aqui.</em>
     */
    @Async
    @SneakyThrows
    public void afterThrowing(final org.springframework.amqp.core.Message message, final Throwable th) {

        ofNullable(message)
            .ifPresent(msg -> {
                ofNullable(msg.getMessageProperties())
                    .ifPresent(mp -> {
                        mp.setTargetBean(null);
                        mp.setTargetMethod(null);
                    });
            });

        ArmazenarMensagemMessage.ArmazenarMensagemMessageBuilder mappedMessage = ArmazenarMensagemMessage.builder();

        mappedMessage
            .traceId(obterTraceId(message.getMessageProperties()))
            .destination(obterDestination(message.getMessageProperties()))
            .error(obterError(th))
            .message(obterMessage(message));

//        rabbitTemplate.send(GFE.getRoutingKey(), withBody(objectMapper.writeValueAsBytes(mappedMessage.build()))
//            .setContentType(MediaType.APPLICATION_JSON_VALUE)
//            .build());
    }

    private Long obterTraceId(final MessageProperties messageProperties) {
        return ofNullable(messageProperties)
            .map(mp -> (Long) mp.getHeaders().get(Span.TRACE_ID_NAME))
            .orElse(null);
    }

    private Destination obterDestination(final MessageProperties messageProperties) {
        Destination.DestinationBuilder destinationBuilder = Destination.builder();

        ofNullable(messageProperties)
            .ifPresent(mp -> {
                destinationBuilder.exchange(mp.getReceivedExchange());
                destinationBuilder.queue(mp.getConsumerQueue());
                destinationBuilder.routingKey(mp.getReceivedRoutingKey());
                destinationBuilder
                    .typeId(ofNullable(mp.getHeaders()).map(headers -> (String) headers.get(DEFAULT_CLASSID_FIELD_NAME))
                        .orElse(null));
            });

        return destinationBuilder.build();
    }

    private Error obterError(final Throwable th) {
        return Error.builder()
            .cause(format("%s: %s", th.getMessage(), ofNullable(th.getCause()).map(t -> t.getMessage()).orElse("")))
            .timestamp(now())
            .build();
    }

    private Message obterMessage(final org.springframework.amqp.core.Message message) {
        return Message.builder()
            .body(Base64.getEncoder().encodeToString(message.getBody()))
            .properties(message.getMessageProperties())
            .build();
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class ArmazenarMensagemMessage implements Serializable {

        private static final long serialVersionUID = -7054086875783313481L;

        private final Long traceId;

        private final Destination destination;

        private final ListenerExceptionInterceptor.Message message;

        private final Error error;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Destination implements Serializable {

        private static final long serialVersionUID = -6245971774227031260L;

        private final String exchange;

        private final String queue;

        private final String routingKey;

        private final String typeId;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Error implements Serializable {

        private static final long serialVersionUID = 2593542431607448973L;

        private final String cause;

        private final OffsetDateTime timestamp;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Message implements Serializable {

        private static final long serialVersionUID = -1471079143761520862L;

        private final MessageProperties properties;

        private final String body;
    }
}

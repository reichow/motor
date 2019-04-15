package br.com.camila.motor.interceptor;

import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.RabbitEnabled;
import lombok.AccessLevel;
import lombok.Getter;

@Component
@RabbitEnabled
public class TraceMessageInterceptor implements MethodBeforeAdvice, MessagePostProcessor {

    @Getter(lazy = true, value = AccessLevel.PRIVATE)
    private final Random random = new Random();

    @Autowired
    private Tracer tracer;

    /**
     * Invocado automaticamente pelo Spring Advice Chain antes de uma mensagem entrar no listener.
     *
     * <br/><br/>Adiciona span no contexto de tracing do Sleuth com <em>spanId</em> randômico e <em>traceId</em>
     * conforme o valor {@link Span#TRACE_ID_NAME} do cabeçalho da menasgem.
     *
     * <br/><br/>Se não vier o cabeçalho {@link Span#TRACE_ID_NAME}, será gerado um valor randômico.
     */
    @Override
    public void before(final Method method, final Object[] args, final Object target) throws Throwable {

        final Message message = (Message) args[1];

        final Long traceId = ofNullable(message.getMessageProperties().getHeaders().get(Span.TRACE_ID_NAME))
            .map(o -> (long) o)
            .orElseGet(() -> getRandom().nextLong());

        tracer.continueSpan(Span.builder()
            .traceId(traceId)
            .parent(traceId)
            .spanId(getRandom().nextLong())
            .exportable(false)
            .build());
    }

    /**
     * Invocado automaticamente pelo RabbitTemplate antes de enviar uma mensagem para o broker.
     *
     * <br/><br/>Serve para verificar se a mensagem possui o cabeçalho {@link Span#TRACE_ID_NAME} e, em caso negativo,
     * adicionar.
     *
     * <br/><br/>O valor do cabeçalho será o <u>número</u> do <em>traceId</em> do span corrente. Caso não haja span
     * corrente, será gerado um novo de forma randômica.
     */
    @Override
    public Message postProcessMessage(final Message message) {

        final Map<String, Object> headers = message.getMessageProperties().getHeaders();

        if (headers.get(Span.TRACE_ID_NAME) == null) {

            final Span span = ofNullable(tracer.getCurrentSpan())
                .orElseGet(() -> tracer.createSpan(randomUUID().toString()));

            headers.put(Span.TRACE_ID_NAME, span.getTraceId());
        }

        return message;
    }
}

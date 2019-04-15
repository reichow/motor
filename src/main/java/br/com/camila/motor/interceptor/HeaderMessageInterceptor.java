package br.com.camila.motor.interceptor;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.RabbitEnabled;

@Component
@RabbitEnabled
public class HeaderMessageInterceptor implements MethodBeforeAdvice, MessagePostProcessor {

    private final ThreadLocal<Map<String, Object>> headers = new ThreadLocal<>();

    /**
     * Invocado automaticamente pelo Spring Advice Chain antes de uma mensagem entrar no listener.
     *
     * <br/><br/>Seta os headers da mensagem de entrada num {@link ThreadLocal}, possibilitando recuperar as informações
     * dentro da thread atual.
     */
    @Override
    public void before(final Method method, final Object[] args, final Object target) throws Throwable {

        final Message message = (Message) args[1];

        headers.set(message.getMessageProperties().getHeaders());
    }

    /**
     * Invocado automaticamente pelo RabbitTemplate antes de enviar uma mensagem para o broker.
     *
     * <br/><br/>Aplica os headers salvos na thread local (caso existam) nos headers da mensagem de saída. São
     * adicionados somente os headers que:<ul> <li>Não comecem com <strong>__</strong>;</li> <li>Não existam na mensagem
     * original.</li> </ul>
     */
    @Override
    public Message postProcessMessage(final Message message) {

        final Map<String, Object> threadLocalHeaders = headers.get();
        final Map<String, Object> messageHeaders = message.getMessageProperties().getHeaders();

        ofNullable(threadLocalHeaders).orElseGet(() -> emptyMap())
            .keySet()
            .stream()
            .filter(key -> isFalse(key.startsWith("__")))
            .filter(key -> isFalse(messageHeaders.containsKey(key)))
            .forEach(key -> messageHeaders.put(key, threadLocalHeaders.get(key)));

        return message;
    }
}

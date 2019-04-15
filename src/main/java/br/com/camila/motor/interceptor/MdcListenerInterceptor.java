package br.com.camila.motor.interceptor;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import br.com.camila.motor.annotation.RabbitEnabled;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RabbitEnabled
@Slf4j
public class MdcListenerInterceptor {

    private static final String POINTCUT = "execution(* br.com.rabbit.exemplo.demo..listener.*.*(..))";
    private static final String KEY_ORIGIN = "origin";

    @Around(POINTCUT)
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {

        Set<String> keys = null;

        try {
            keys = addMdc(pjp);
            return pjp.proceed();
        } finally {
            deleteMdc(keys);
        }
    }

    private Set<String> addMdc(final ProceedingJoinPoint pjp) {

        final Set<String> keys = new HashSet<>();

        try {
            asList(pjp.getArgs()).stream()
                .filter(argument -> argument instanceof MessageHeaders)
                .findFirst()
                .map(argument -> (MessageHeaders) argument)
                .ifPresent(headers -> {
                    addKey(KEY_ORIGIN, headers, key -> keys.add(key));
                });

        } catch (final Exception e) { //NOSONAR
            log.warn("Erro ao inserir informações no MDC. Não afeta o fluxo.");
        }

        return keys;
    }

    private void addKey(final String key, final MessageHeaders headers, final Consumer<String> consumerKey) {

        ofNullable(headers.get(key.toUpperCase(), String.class))
            .ifPresent(value -> {
                MDC.put(key, value);
                consumerKey.accept(key);
            });
    }

    private void deleteMdc(final Set<String> keys) {

        ofNullable(keys)
            .ifPresent(k -> keys.forEach(key -> MDC.remove(key)));
    }

}

package br.com.camila.motor.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.*;

@Documented
@Inherited
@ConditionalOnProperty("spring.rabbitmq.enabled")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RabbitEnabled {

}


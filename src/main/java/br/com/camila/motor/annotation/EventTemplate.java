package br.com.camila.motor.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Anotação para RabbitTemplate bean de eventos de motor.
 */
@Documented
@Inherited
@Qualifier("motor-events-rabbit-template")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface EventTemplate {

}


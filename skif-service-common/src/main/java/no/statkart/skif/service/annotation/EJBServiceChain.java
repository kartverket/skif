package no.statkart.skif.service.annotation;


import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation som brukes til å anngi objekt som har EJB Service Chain proxies foran seg.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface EJBServiceChain {
}
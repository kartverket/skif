package no.statkart.skif.service.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation som angir at parameteren overføres via ServiceContext istedet for en selvstendig parameter
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@BindingAnnotation
@Target({PARAMETER})
@Retention(RUNTIME)
public @interface ServiceContextMapped {
}

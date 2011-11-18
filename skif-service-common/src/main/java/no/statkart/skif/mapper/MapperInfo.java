package no.statkart.skif.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//import static java.lang.annotation.ElementType.TYPE;

/**
 * @author Henrik Fredholm
 */
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface MapperInfo {
    Class[] value();
}
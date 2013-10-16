package no.statkart.skif.mapper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Henrik Fredholm
 * @deprecated mapperen kan finne denne informasjonen via reflection
 */
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
@Deprecated
public @interface MapperInfo {
    Class[] value();
}
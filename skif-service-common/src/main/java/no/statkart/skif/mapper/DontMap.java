package no.statkart.skif.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotasjon som kan plasseres på gettere og/eller settere for å indikere at egenskapen ikke skal mappes.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DontMap {
}

package no.statkart.skif.util;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotasjon som kan brukes for å si hvilken versjon en klasse eller et felt ble introdusert i.
 *
 * @author Roar Ingebrigtsen
 * @since 2.7
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Since {

   String value();

}

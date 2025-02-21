package no.statkart.skif.service.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation som angir at parameteren skal ignoreres av algoritmen som bestemmer hvilken  {@code SnapshotVersion}
 * som skal brukes for kallet. Hvis annotasjonen legges på selve metoden så betyder det at metodekallet skal anvende
 * {@code SnapshotVersion} for kjørende tråd
 *
 * Hvordan SnapshotVersion bestemmes for kjørende tråd avhenger av implementasjonen til {@code D2WAdapterProxyHandler} og
 * {@code W2DAdapterProxyHandler}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@Target({PARAMETER, METHOD})
@Retention(RUNTIME)
public @interface SuppressSnapshotVersionMapping {
}

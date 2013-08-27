package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt er en komponent som ikke har en id i det hele tatt. Objektet lagres i
 * databasen i samme tabell som det eiende objektet. CompositeComponent bør ikke brukes som elementer i collections.
 * I stedet bør man bruke {@link ValueObject} eller {@link EntityComponent} da disse løser livssyklus problemer
 * rundt delete-orphan og ungår eskallert bruk av composite-nøkler for indre komponenter og collections.
 *
 * <P>CompositeComponent kan inneholde andre ValueObject-er og EntityComponent-er samt collections av disse. En CompositeComponent
 * kan også inneholde andre CompositeComponent-er, men ikke collections av disse. Mange nestede nivåer at CompositeComponent-er
 * bør unngårs.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.2.3
 */
public interface CompositeComponent<T> extends ComponentWithOwnerReference<T> {
}

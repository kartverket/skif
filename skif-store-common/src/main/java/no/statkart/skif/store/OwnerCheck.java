package no.statkart.skif.store;

/**
 * Interface metode for å sjekke at owner peker på child
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public interface OwnerCheck<O, C extends ComponentWithOwnerReference<O>> {
    boolean apply(O owner, C child);
}

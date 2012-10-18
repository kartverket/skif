package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt er en komponent som har en id som er eksplisitt definer i domeneobjektet
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ComponentWithExplicitId<I extends Serializable> extends Identifiable<I>, Component {
}

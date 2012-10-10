package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface for domeneobjekter som er EntityComponents. EntityComponents er objekter som tilhører en annet objekt og
 * hvis livssyklus er styrt av dette objektetr. EntityCompnents har id som er en del av domeneobjektet.
 *
 * Implementasjon av dette interfacet indikerer at objektet ikke er et BubbleObject eller en ValueComponent
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface EntityComponent<I extends Serializable> extends Identifiable<Identifiable> {
}

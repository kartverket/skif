package no.statkart.skif.store;

/**
 * Interface som CompositeComponet kan implementere dersom objektet har collections og det er ønskelig å kunne
 * behandle og erstattes med null dersom dets felter er null. Dette er nemlig oppførslen til Hibernate hvis
 * objektet ikke inneholder Collection felter. Når komponenten inneholder Collection felter så opprette
 * Hibernate alltid komponenten også selve om alle felter er null og Collection feltene er tomme. Dette interfacet
 * gjør det mulig å behandle componenten som om den var null og om ønskelig å erstatte den med null.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public interface CompositeComponentWithCollections {
    /**
     * Returnerer true hvis det er lov å behandle componenten som om den var null og evt erstatte den med null.
     */
    boolean isNullComponent();
}

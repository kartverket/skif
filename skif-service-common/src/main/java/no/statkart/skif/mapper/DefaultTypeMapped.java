package no.statkart.skif.mapper;

/**
 * Interface for mapping som støtter {@link DefaultTypeMapping}. {@link Mapping}-interfacet extender dette interfacet
 * for å signalisere dette.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface DefaultTypeMapped {
    DefaultTypeMapping getDefaultTypeMapping();
}

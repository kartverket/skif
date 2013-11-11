package no.statkart.skif.mapper;

/**
 * Interface for at mappere skal kunne kommunisere med AbstractMapper.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.1
 */
public interface MappingBase {
    /**
     * Dersom man har sirkluære referanser på begge sider av en mapping, er det viktig at man registrerer target slik
     * at man kan gjenbruke referansen senere. {@link DefaultTypeMapper} gjør dette. Det er viktig at target
     * registereres før feltene dens mappes.
     *
     * @param source    objekt det mappes fra
     * @param target    objekt det mappes til
     */
    void registerTarget(Object source, Object target);

    /**
     * Gir tilgang til {@link MappingResolver}, dersom angitt, slik at man kan finne ut hvilke klasser som tilsvarer hverandre.
     */
    MappingResolver getMappingResolver();
}

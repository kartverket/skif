package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;

/**
 * Interface for å opprette {@link TypeMapper}e dynamisk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface TypeMapperFactory {
    /**
     * Prøver å opprette en mapper mellom gitte typer.
     *
     *
     * @param wsapiTypeToken     Web service-type
     * @param domainTypeToken    Domenetype
     * @return typemapper, eller <code>null</code> dersom den ikke vet hvordan lage en mapper for gitte typer
     */
    <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken);
}

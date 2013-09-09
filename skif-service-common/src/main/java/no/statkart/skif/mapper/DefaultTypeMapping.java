package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;

/**
 * Interface for å gjøre automagisk mapping. Dette er en snarvei ned til {@link DefaultTypeMapper}, slik at en custom
 * mapper kan avlaste mesteparten av jobben til den. Hvis den prøver å gjøre dette via {@link Mapping}, vil den bare gå
 * i uendelig rekursjon.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface DefaultTypeMapping {
    /**
     * Ber om automagisk mapping fra interndomene til WS-domene.
     *
     * @param source    internobjekt
     * @param wsapiType detaljert typeinformasjon for ønsket WS-objekt, som er viktig for generiske typer (bruk {@link TypeToken#of(Class)} for enkle klasser)
     * @return WS-objekt
     */
    <WsapiT> WsapiT mapDomainObject(Object source, TypeToken<WsapiT> wsapiType);

    /**
     * Ber om automagisk mapping fra WS-domene til interndomene.
     *
     * @param source     WS-objekt
     * @param domainType detaljert typeinformasjon for ønsket internobjekt, som er viktig for generiske typer (bruk {@link TypeToken#of(Class)} for enkle klasser)
     * @return internobjekt
     */
    <DomainT> DomainT mapWsapiObject(Object source, TypeToken<DomainT> domainType);
}

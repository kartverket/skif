package no.statkart.skif.mapper;


/**
 * Interface for mapping mellom to objekttyper.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public interface TypeMapper<WsapiT, DomainT> {

    Mapping getMapping();

    void setMapping(Mapping mapping);

    WsapiT mapDomainObject(DomainT source);

    DomainT mapWsapiObject(WsapiT source);

    Class<WsapiT> getWsapiClass();

    Class<DomainT> getDomainClass();

}
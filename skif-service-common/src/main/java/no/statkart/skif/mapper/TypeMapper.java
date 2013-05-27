package no.statkart.skif.mapper;


/**
 * Interface for mapping mellom to objekttyper.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public interface TypeMapper<WsapiT,DomainT> {
    public Mapping getMapping();
    void setMapping(Mapping mapping);

    public WsapiT mapDomainObject(DomainT source);

    public DomainT mapWsapiObject(WsapiT source);

    Class<WsapiT> getWsapiClass();

    Class<DomainT> getDomainClass();
}
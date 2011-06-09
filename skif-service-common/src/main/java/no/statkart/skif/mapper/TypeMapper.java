package no.statkart.skif.mapper;


/**
 * Interface for mapping2 mellom to objekttyper
 * @author Henrik Fredholm
 */
public interface TypeMapper<WsapiT,DomainT> {
    public Mapping getMapping();
    void setMapping(Mapping mapping);

    public ObjectFactory getDomainObjectFactory();
    public void setDomainObjectFactory(ObjectFactory factory);

    public ObjectFactory getWsapiObjectFactory();
    public void setWsapiObjectFactory(ObjectFactory factory);

    public Class<WsapiT> getWsapiClass();
    public Class<DomainT> getDomainClass();

    public WsapiT mapDomainObject(DomainT source);
    public void mapDomainObject(DomainT source, WsapiT target);

    public DomainT mapWsapiObject(WsapiT source);
    public void mapWsapiObject(WsapiT source, DomainT target) ;
}
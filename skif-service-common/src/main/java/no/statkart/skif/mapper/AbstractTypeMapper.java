package no.statkart.skif.mapper;


/**
 * Bare veldig grunnleggende basisfunksjonalitet i forhold til
 *
 * @param <WsapiT>  klassen i WS-API-domenet
 * @param <DomainT> klassen i Java-domenet
 * @param <M>       mapping-interfacet denne mapperen benytter
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public abstract class AbstractTypeMapper<WsapiT, DomainT, M extends Mapping> implements TypeMapper<WsapiT, DomainT> {
    private final Class<WsapiT> wsapiClass;
    private final Class<DomainT> domainClass;
    private final Class<? extends M> mappingInterface;

    private M mapping;

    /**
     * @param wsapiClass       klassen i WS-API-domenet
     * @param domainClass      klassen i Java-domenet
     * @param mappingInterface mapping-interfacet denne mapperen benytter
     */
    protected AbstractTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<? extends M> mappingInterface) {
        this.wsapiClass = wsapiClass;
        this.domainClass = domainClass;
        this.mappingInterface = mappingInterface;
    }

    @Override
    public M getMapping() {
        return mapping;
    }

    @Override
    public void setMapping(Mapping mapping) {
        try {
            this.mapping = mappingInterface.cast(mapping);
        } catch (ClassCastException e) {
            throw new MappingException("Can not use mapper which requires mapping " + mappingInterface + " with mapping " + mapping, e);
        }
    }

    @Override
    public Class<WsapiT> getWsapiClass() {
        return wsapiClass;
    }

    @Override
    public Class<DomainT> getDomainClass() {
        return domainClass;
    }

    /**
     * Hjelpemetode for å opprette klasse av typen WsapiT. Dette virker bare dersom WsapiT er default constructable.
     *
     * @return ny instans
     * @throws MappingException dersom ny instans ikke kan opprettes
     */
    protected WsapiT createWsapiT() {
        try {
            return wsapiClass.newInstance();
        } catch (InstantiationException e) {
            throw new MappingException("Could not create new instance of " + wsapiClass, e);
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not create new instance of " + wsapiClass, e);
        }
    }

    /**
     * Hjelpemetode for å opprette klasse av typen DomainT. Dette virker bare dersom DomainT er default constructable.
     *
     * @return ny instans
     * @throws MappingException dersom ny instans ikke kan opprettes
     */
    protected DomainT createDomainT() {
        try {
            return domainClass.newInstance();
        } catch (InstantiationException e) {
            throw new MappingException("Could not create new instance of " + domainClass, e);
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not create new instance of " + domainClass, e);
        }
    }

    /**
     * @return debug streng på formen <pre>&lt;TypeMapperklasse&gt;{&lt;domeneklasse&gt; &lt;-&gt; &lt;apiklasse&gt;}</pre>
     */
    public String toString() {
        return super.toString() + "{" + domainClass + " <-> " + wsapiClass + "}";
    }
}
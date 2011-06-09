package no.statkart.skif.mapper;

/**
 * Mapping Metoder som en {@code TypeMapper} må implementere eksplisitt
 * @author Henrik Fredholm
 */
public interface BaseMapping {
    public ObjectFactory getDomainObjectFactory();
    public ObjectFactory getWsapiObjectFactory();
}

package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;

/**
 * @author Tor Egil R. Strand
 */
public interface AutomaticTypeMapper {
    public Mapping getMapping();
    void setMapping(Mapping mapping);

    public Object mapDomainObject(Object source, TypeToken<?> Objectype);

    public Object mapWsapiObject(Object source, TypeToken<?> Objectype);
}

package no.statkart.skif.mapper;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Mapper {@link URL} til xs:string.
 */
public class UrlTypeMapper extends AbstractTypeMapper<String, URL, Mapping> {
    public UrlTypeMapper() {
        super(String.class, URL.class, Mapping.class);
    }

    @Override
    public String mapDomainObject(URL source) {
        return source.toString();
    }

    @Override
    public URL mapWsapiObject(String source) {
        try {
            return new URL(source);
        } catch (MalformedURLException e) {
            throw new MappingException("Error mapping URL: " + source, e);
        }
    }
}

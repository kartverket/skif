package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;

/**
 * Mapper {@link Class} til {@link String}.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class ClassTypeMapper extends AbstractTypeMapper<String, Class, Mapping> {

    private static final TypeToken<Object> objectTypeToken = TypeToken.of(Object.class);

    public ClassTypeMapper() {
        super(String.class, Class.class, Mapping.class);
    }

    @Override
    public String mapDomainObject(Class source) {
        MappingResolver mappingResolver = getMapping().getMappingResolver();
        if (mappingResolver != null) {
            TypeToken<?> typeToken = mappingResolver.resolveTargetType(source, objectTypeToken);
            return typeToken.getRawType().getName();
        } else {
            return source.getName();
        }
    }

    @Override
    public Class mapWsapiObject(String source) {
        final Class clazz;
        try {
            clazz = Class.forName(source);
        } catch (ClassNotFoundException e) {
            throw new MappingException("No class for name " + source, e);
        }

        MappingResolver mappingResolver = getMapping().getMappingResolver();
        if (mappingResolver != null) {
            return mappingResolver.resolveTargetType(clazz, objectTypeToken).getRawType();
        } else {
            return clazz;
        }
    }
}

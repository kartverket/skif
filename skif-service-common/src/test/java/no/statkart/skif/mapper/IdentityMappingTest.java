package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Sjekker ting rundt mapping av basistyper, som bare skal tvert igjennom.
 */
public class IdentityMappingTest {
    /**
     * I dette tilfellet har vi en {@link Integer} på source-siden, men vet ikke noe mer enn at det er {@link Object}
     * på andre siden. Det er en {@link IdentityTypeMapperFactory} installert som skal sørge for at {@code Integer}
     * kommer rett over.
     * <p>
     * Dette er noe som ikke virker så minimalt som det er gjort her.
     */
    @Test(enabled = false)
    public void testIntegerToObject() {
        AbstractMapper<Mapping> mapper = new AbstractMapper<Mapping>(Mapping.class) {
            {
                IdentityTypeMapperFactory identityTypeMapperFactory = new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes();
                addMapperFactory(identityTypeMapperFactory);
            }
        };
        Mapping mapping = mapper.getMapping();

        Integer i1 = 1;

        Object o = mapping.d2w(i1, Object.class);

        Assert.assertEquals(o.getClass(), Integer.class);
        Assert.assertEquals(o, i1);
        Assert.assertEquals(mapper.mapperCache.keySet().iterator().next(), new AbstractMapper.MapperKey(TypeToken.of(Object.class), TypeToken.of(Integer.class)));
    }

    /**
     * I dette tilfellet har vi en {@link Integer} på source-siden, men vet ikke noe mer enn at det er {@link Object}
     * på andre siden. Det er en {@link IdentityTypeMapperFactory} installert som skal sørge for at {@code Integer}
     * kommer rett over. For å få dette til å virke er det brukt noen overrides, som {@code IdentityTypeMapperFactory}
     * lager for oss.
     */
    @Test
    public void testIntegerToObjectWithOverrides() {
        AbstractMapper<Mapping> mapper = new AbstractMapper<Mapping>(Mapping.class) {
            {
                IdentityTypeMapperFactory identityTypeMapperFactory = new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes();
                addMapperFactory(identityTypeMapperFactory);
                MappingResolver mappingResolver = new MappingResolver();
                mappingResolver.overrideClassMappings(identityTypeMapperFactory.getOverrideMappings());
                setMappingResolver(mappingResolver);
            }
        };
        Mapping mapping = mapper.getMapping();

        Integer i1 = 1;

        Object o = mapping.d2w(i1, Object.class);

        Assert.assertEquals(o.getClass(), Integer.class);
        Assert.assertEquals(o, i1);
        Assert.assertEquals(mapper.mapperCache.keySet().iterator().next(), new AbstractMapper.MapperKey(TypeToken.of(Integer.class), TypeToken.of(Integer.class)));
    }

    /**
     * I dette tilfellet har vi {@link Integer} som source og {@code int} som target, men installerer en override class
     * mapping for {@link Integer} til {@code Integer}.
     */
    @Test
    public void testIntToIntWithIntegerOverride() {
        AbstractMapper<Mapping> mapper = new AbstractMapper<Mapping>(Mapping.class) {
            {
                MappingResolver mappingResolver = new MappingResolver();
                Map<Class<?>, Class<?>> overrides = new HashMap<Class<?>, Class<?>>(1);
                overrides.put(Integer.class, Integer.class);
                mappingResolver.overrideClassMappings(overrides);
                setMappingResolver(mappingResolver);

                addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());
            }
        };
        Mapping mapping = mapper.getMapping();

        int i1 = 1;

        Object o = mapping.d2w(i1, Integer.class, Integer.TYPE);

        Assert.assertEquals(o.getClass(), Integer.class);
        Assert.assertEquals(o, i1);
        Assert.assertEquals(mapper.mapperCache.keySet().iterator().next(), new AbstractMapper.MapperKey(TypeToken.of(Integer.class), TypeToken.of(Integer.class)));
    }

    /**
     * I dette tilfellet har vi også {@link Integer} som source og {@code int} som target, men ingen override.
     */
    @Test
    public void testIntToIntWithoutIntegerOverride() {
        AbstractMapper<Mapping> mapper = new AbstractMapper<Mapping>(Mapping.class) {
            {
                addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());
            }
        };
        Mapping mapping = mapper.getMapping();

        int i1 = 1;

        Object o = mapping.d2w(i1, Integer.class, Integer.TYPE);

        Assert.assertEquals(o.getClass(), Integer.class);
        Assert.assertEquals(o, i1);
        Assert.assertEquals(mapper.mapperCache.keySet().iterator().next(), new AbstractMapper.MapperKey(TypeToken.of(Integer.TYPE), TypeToken.of(Integer.class)));
    }
}

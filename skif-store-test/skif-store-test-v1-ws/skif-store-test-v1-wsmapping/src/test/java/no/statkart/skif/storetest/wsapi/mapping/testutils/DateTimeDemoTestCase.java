package no.statkart.skif.storetest.wsapi.mapping.testutils;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DateTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.SQLDateTypeMapper;
import no.statkart.skif.mapper.SQLTimeTypeMapper;
import no.statkart.skif.mapper.SQLTimestampTypeMapper;
import no.statkart.skif.mapper.TypeMapper;
import org.testng.annotations.BeforeMethod;

/**
 *
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 29
 */
public abstract class DateTimeDemoTestCase {

    protected AbstractMapper<Mapping> mapper;

    @BeforeMethod
    public void setUpMappe() {
        mapper = buildMapping();
    }

    protected abstract AbstractMapper<Mapping> buildMapping();


    protected TestMapper mapperForTypeMappers(TypeMapper<?, ?>... typeMappers) {
        return new TestMapper(typeMappers);
    }

    class TestMapper extends AbstractMapper<Mapping> {
        TestMapper(TypeMapper<?, ?>... typeMappers) {
            super(Mapping.class);

//            setMappingResolver(new MappingResolver());
//            getMappingResolver().addPackageMapping(no.statkart.skif.storetest.wsapi.service.testmapping.DateTimeDemoImpl1.class.getPackage().getName(), DateTimeDemoImpl1.class.getPackage().getName());
//            addMapperFactory(new DefaultTypeMapperFactory());

            for (TypeMapper typeMapper : typeMappers) {
                addMapper(typeMapper);
            }

            addMapper(new SQLTimeTypeMapper());
            addMapper(new SQLDateTypeMapper());
            addMapper(new SQLTimestampTypeMapper());
            addMapper(new DateTypeMapper());

        }
    }
}

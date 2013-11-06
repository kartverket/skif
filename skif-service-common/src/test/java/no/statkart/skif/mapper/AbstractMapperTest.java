package no.statkart.skif.mapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Diverse tester for AbstractTypeMapper.
 *
 * @author Tor Egil R. Strand
 * @since 2.3.1
 */
@Test
public class AbstractMapperTest {
    // SKIF-383
    public void testMultiplePossibleMappers() {
        List<TypeMapper<?, ?>> typeMappers = Arrays.<TypeMapper<?, ?>>asList(
                new TimestampTypeMapper(),
                new DateTypeMapper()
        );

        TypeMapper<?, ?> closestTypeMapper = AbstractMapper.findClosestTypeMapper(typeMappers, Date.class, AbstractMapper.Direction.W2D);

        Assert.assertEquals(closestTypeMapper.getClass(), DateTypeMapper.class);
    }
}
